package dev.sterner.brewinandchewin.common.block.entity;

import com.google.common.collect.Lists;
import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemHandler;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackHandler;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.RecipeManagerAccessorMixin;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import dev.sterner.brewinandchewin.common.block.KegBlock;
import dev.sterner.brewinandchewin.common.block.screen.KegBlockScreenHandler;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import dev.sterner.brewinandchewin.common.registry.BCTags;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KegBlockEntity extends SyncedBlockEntity implements ExtendedScreenHandlerFactory, Nameable {
    public static final int FLUID_ITEM_INPUT_SLOT = 4;
    public static final int DRINK_DISPLAY_SLOT = 5;
    public static final int CONTAINER_SLOT = 6;
    public static final int OUTPUT_SLOT = 7;
    public static final int OUTPUT_CONTAINER_SLOT = 8;
    public static final int INVENTORY_SIZE = 9;

    private final ItemStackHandler inventory;

    private int fermentTime;
    private int fermentTimeTotal;
    private ItemStack drinkContainerStack;
    private Text customName;
    public int kegTemperature;
    protected final PropertyDelegate kegData;
    private final Object2IntOpenHashMap<Identifier> usedRecipeTracker;
    private Identifier lastRecipeID;
    private boolean checkNewRecipe;

    public KegBlockEntity( BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.KEG, pos, state);
        this.inventory = createHandler();
        this.drinkContainerStack = ItemStack.EMPTY;
        this.kegData = createIntArray();
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.checkNewRecipe = true;
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        inventory.readNbt(compound.getCompound("Inventory"));
        fermentTime = compound.getInt("FermentTime");
        fermentTimeTotal = compound.getInt("FermentTimeTotal");
        drinkContainerStack = ItemStack.fromNbt(compound.getCompound("Container"));
        kegTemperature = compound.getInt("Temperature");
        if (compound.contains("CustomName", 8)) {
            customName = Text.Serializer.fromJson(compound.getString("CustomName"));
        }
        NbtCompound compoundRecipes = compound.getCompound("RecipesUsed");
        for (String key : compoundRecipes.getKeys()) {
            usedRecipeTracker.put(new Identifier(key), compoundRecipes.getInt(key));
        }
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.putInt("FermentTime", fermentTime);
        compound.putInt("FermentTimeTotal", fermentTimeTotal);
        compound.put("Container", drinkContainerStack.writeNbt(new NbtCompound()));
        compound.putInt("Temperature", kegTemperature);
        if (customName != null) {
            compound.putString("CustomName", Text.Serializer.toJson(customName));
        }
        compound.put("Inventory", inventory.writeNbt(new NbtCompound()));
        NbtCompound compoundRecipes = new NbtCompound();
        usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        compound.put("RecipesUsed", compoundRecipes);
    }

    private NbtCompound writeItems(NbtCompound compound) {
        writeNbt(compound);
        compound.put("Container", drinkContainerStack.writeNbt(new NbtCompound()));
        compound.put("Inventory", inventory.writeNbt(new NbtCompound()));
        return compound;
    }

    public NbtCompound writeMeal(NbtCompound compound) {
        if (getDrink().isEmpty()) return compound;

        ItemStackHandler drops = new ItemStackHandler(INVENTORY_SIZE);
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.setStack(i, i == DRINK_DISPLAY_SLOT ? inventory.getStack(i) : ItemStack.EMPTY);
        }
        if (customName != null) {
            compound.putString("CustomName", Text.Serializer.toJson(customName));
        }
        compound.put("Container", drinkContainerStack.writeNbt(new NbtCompound()));
        compound.put("Inventory", drops.writeNbt(new NbtCompound()));
        return compound;
    }

    // ======== BASIC FUNCTIONALITY ========

    public static void fermentingTick(World level, BlockPos pos, BlockState state, KegBlockEntity keg) {
        boolean didInventoryChange = false;
        keg.updateTemperature();
        if (keg.hasInput()) {
            Optional<KegRecipe> recipe = keg.getMatchingRecipe(new RecipeWrapper(keg.inventory));
            if (recipe.isPresent() && keg.canFerment((KegRecipe)recipe.get())) {
                didInventoryChange = keg.processFermenting((KegRecipe)recipe.get(), keg);
            } else {
                keg.fermentTime = 0;
            }
        } else if (keg.fermentTimeTotal > 0) {
            keg.fermentTime = MathHelper.clamp(keg.fermentTime - 2, 0, keg.fermentTimeTotal);
        }

        ItemStack drinkStack = keg.getDrink();
        if (!drinkStack.isEmpty()) {
            if (!keg.doesDrinkHaveContainer(drinkStack)) {
                keg.moveDrinkToOutput();
                didInventoryChange = true;
            } else if (!keg.inventory.getStack(6).isEmpty()) {
                keg.useStoredContainersOnMeal();
                didInventoryChange = true;
            }
        }

        if (didInventoryChange) {
            keg.inventoryChanged();
        }

    }

    public void updateTemperature() {
        if (this.world != null && this.world.getDimension().ultrawarm()) {
            this.kegTemperature = 10;
            return;
        }

        ArrayList<BlockState> states = new ArrayList();
        int range = 1;

        int heat;
        int cold;
        for(heat = -range; heat <= range; ++heat) {
            for(int y = -range; y <= range; ++y) {
                for(cold = -range; cold <= range; ++cold) {
                    states.add(this.world.getBlockState(this.getPos().add(heat, y, cold)));
                }
            }
        }

        heat = states.stream().filter((s) -> {
            return s.isIn(TagsRegistry.HEAT_SOURCES);
        }).filter((s) -> {
            return s.contains(Properties.LIT);
        }).filter((s) -> {
            return s.get(Properties.LIT);
        }).mapToInt((s) -> {
            return 1;
        }).sum();
        heat += states.stream().filter((s) -> {
            return s.isIn(TagsRegistry.HEAT_SOURCES);
        }).filter((s) -> {
            return !s.get(Properties.LIT);
        }).mapToInt((s) -> {
            return 1;
        }).sum();
        BlockState stateBelow = this.world.getBlockState(this.getPos().down());
        if (stateBelow.isIn(TagsRegistry.HEAT_CONDUCTORS)) {
            BlockState stateFurtherBelow = this.world.getBlockState(this.getPos().down(2));
            if (stateFurtherBelow.isIn(TagsRegistry.HEAT_SOURCES)) {
                if (stateFurtherBelow.get(Properties.LIT)) {
                    if (stateFurtherBelow.get(Properties.LIT)) {
                        ++heat;
                    }
                } else {
                    ++heat;
                }
            }
        }

        cold = states.stream().filter((s) -> {
            return s.isIn(BCTags.FREEZE_SOURCES);
        }).mapToInt((s) -> {
            return 1;
        }).sum();
        float biomeTemperature = this.world.getBiome(this.getPos()).value().getTemperature();
        if (biomeTemperature <= 0.0F) {
            cold += 2;
        } else if (biomeTemperature == 2.0F) {
            heat += 2;
        }

        this.kegTemperature = heat - cold;

    }

    public int getTemperature() {
        if (this.kegTemperature < -4) {
            return 1;
        } else if (this.kegTemperature < -1) {
            return 2;
        } else if (this.kegTemperature < 2) {
            return 3;
        } else {
            return this.kegTemperature < 5 ? 4 : 5;
        }
    }

    public static void animationTick(World level, BlockPos pos, BlockState state, KegBlockEntity keg) {
    }

    private Optional<KegRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (world == null) return Optional.empty();

        if (lastRecipeID != null) {
            Recipe<Inventory> recipe = ((RecipeManagerAccessorMixin) world.getRecipeManager())
                    .getAllForType(BCRecipeTypes.KEG_RECIPE_TYPE)
                    .get(lastRecipeID);
            if (recipe != null) {
                if (recipe.matches(inventoryWrapper, world)) {
                    return Optional.of((KegRecipe) recipe);
                }
                if (recipe.getOutput().isItemEqual(getDrink())) {
                    return Optional.empty();
                }
            }
        }

        if (checkNewRecipe) {
            Optional<KegRecipe> recipe = world.getRecipeManager().getFirstMatch(BCRecipeTypes.KEG_RECIPE_TYPE, inventoryWrapper, world);
            if (recipe.isPresent()) {
                lastRecipeID = recipe.get().getId();
                return recipe;
            }
        }

        checkNewRecipe = false;
        return Optional.empty();
    }

    public ItemStack getContainer() {
        if (!drinkContainerStack.isEmpty()) {
            return drinkContainerStack;
        } else {
            return new ItemStack(getDrink().getItem().getRecipeRemainder());
        }
    }

    private boolean hasInput() {
        for (int i = 0; i < DRINK_DISPLAY_SLOT; ++i) {
            if (!inventory.getStack(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    protected boolean canFerment(KegRecipe recipe) {
        int recipeTemp = recipe.getTemperature();
        if (this.hasInput() && (recipeTemp == 3 || recipeTemp == this.getTemperature())) {
            ItemStack resultStack = recipe.getOutput();
            if (resultStack.isEmpty()) {
                return false;
            } else {
                ItemStack fluidStack = this.inventory.getStack(FLUID_ITEM_INPUT_SLOT);
                ItemStack storedContainerStack = this.inventory.getStack(OUTPUT_CONTAINER_SLOT);
                if (!storedContainerStack.isEmpty() && fluidStack.getItem().getRecipeRemainder() != null && !fluidStack.getItem().getRecipeRemainder().equals(storedContainerStack.getItem())) {
                    return false;
                } else if (storedContainerStack.getCount() >= storedContainerStack.getMaxCount()) {
                    return false;
                } else {
                    ItemStack storedDrinkStack = this.inventory.getStack(5);
                    if (storedDrinkStack.isEmpty()) {
                        return true;
                    } else if (!storedDrinkStack.isItemEqual(resultStack)) {
                        return false;
                    } else if (storedDrinkStack.getCount() + resultStack.getCount() <= this.inventory.getMaxCountForSlot(5)) {
                        return true;
                    } else {
                        return storedDrinkStack.getCount() + resultStack.getCount() <= resultStack.getMaxCount();
                    }
                }
            }
        } else {
            return false;
        }
    }

    private boolean processFermenting(KegRecipe recipe, KegBlockEntity keg) {
        if (this.world == null) {
            return false;
        } else {
            ++this.fermentTime;
            this.fermentTimeTotal = recipe.getFermentTime();
            if (this.fermentTime < this.fermentTimeTotal) {
                return false;
            } else {
                this.fermentTime = 0;
                this.drinkContainerStack = recipe.getOutputContainer();
                ItemStack resultStack = recipe.getOutput();
                ItemStack storedMealStack = this.inventory.getStack(5);
                if (storedMealStack.isEmpty()) {
                    this.inventory.setStack(5, resultStack.copy());
                } else if (storedMealStack.isItemEqual(resultStack)) {
                    storedMealStack.increment(resultStack.getCount());
                }

                ItemStack storedContainers = this.inventory.getStack(OUTPUT_CONTAINER_SLOT);
                ItemStack fluidStack = this.inventory.getStack(FLUID_ITEM_INPUT_SLOT);
                if (storedContainers.isEmpty()) {
                    this.inventory.setStack(OUTPUT_CONTAINER_SLOT, fluidStack.copy().getRecipeRemainder());
                    if (fluidStack.getCount() == 1) {
                        this.inventory.setStack(FLUID_ITEM_INPUT_SLOT, ItemStack.EMPTY);
                    } else {
                        this.inventory.setStack(FLUID_ITEM_INPUT_SLOT, new ItemStack(fluidStack.getItem(), fluidStack.getCount() - 1));
                    }
                } else if (storedContainers.isItemEqual(this.inventory.getStack(FLUID_ITEM_INPUT_SLOT).getRecipeRemainder())) {
                    storedContainers.increment(resultStack.getCount());
                    if (fluidStack.getCount() == 1) {
                        this.inventory.setStack(FLUID_ITEM_INPUT_SLOT, ItemStack.EMPTY);
                    } else {
                        this.inventory.setStack(FLUID_ITEM_INPUT_SLOT, new ItemStack(fluidStack.getItem(), fluidStack.getCount() - 1));
                    }
                }

                keg.setLastRecipe(recipe);

                for(int i = 0; i < 4; ++i) {
                    ItemStack slotStack = this.inventory.getStack(i);
                    if (slotStack.getItem().hasRecipeRemainder()) {
                        Direction direction = this.getCachedState().get(KegBlock.FACING).rotateYCounterclockwise();
                        double x = (double)this.getPos().getX() + 0.5 + (double)direction.getOffsetX() * 0.25;
                        double y = (double)this.getPos().getY() + 0.7;
                        double z = (double)this.getPos().getZ() + 0.5 + (double)direction.getOffsetZ() * 0.25;

                        ItemEntity entity = new ItemEntity(world, x, y, z, this.inventory.getStack(i).getRecipeRemainder());
                        entity.setVelocity(((float)direction.getOffsetX() * 0.08F), 0.25, ((float)direction.getOffsetZ() * 0.08F));
                        world.spawnEntity(entity);
                    }

                    if (!slotStack.isEmpty()) {
                        slotStack.decrement(1);
                    }
                }

                return true;
            }
        }
    }

    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier recipeID = recipe.getId();
            usedRecipeTracker.addTo(recipeID, 1);
        }
    }

    public void unlockLastRecipe(PlayerEntity player) {
        List<Recipe<?>> usedRecipes = this.getUsedRecipesAndPopExperience(player.world, player.getPos());
        player.unlockRecipes(usedRecipes);
        this.usedRecipeTracker.clear();
    }

    public List<Recipe<?>> getUsedRecipesAndPopExperience(World level, Vec3d pos) {
        List<Recipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<Identifier> identifierEntry : this.usedRecipeTracker.object2IntEntrySet()) {
            level.getRecipeManager().get(identifierEntry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience((ServerWorld) level, pos, identifierEntry.getIntValue(), ((KegRecipe) recipe).getExperience());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(ServerWorld level, Vec3d pos, int craftedAmount, float experience) {
        int expTotal = MathHelper.floor((float)craftedAmount * experience);
        float expFraction = MathHelper.fractionalPart((float)craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double)expFraction) {
            ++expTotal;
        }

        ExperienceOrbEntity.spawn(level, pos, expTotal);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ItemStack getDrink() {
        return this.inventory.getStack(5);
    }

    public DefaultedList<ItemStack> getDroppableInventory() {
        DefaultedList<ItemStack> drops = DefaultedList.of();

        for(int i = 0; i < 9; ++i) {
            if (i != 5) {
                drops.add(this.inventory.getStack(i));
            }
        }

        return drops;
    }

    private void moveDrinkToOutput() {
        ItemStack mealStack = inventory.getStack(DRINK_DISPLAY_SLOT);
        ItemStack outputStack = inventory.getStack(OUTPUT_SLOT);
        int mealCount = Math.min(mealStack.getCount(), mealStack.getMaxCount() - outputStack.getCount());
        if (outputStack.isEmpty()) {
            inventory.setStack(OUTPUT_SLOT, mealStack.split(mealCount));
        } else if (outputStack.getItem() == mealStack.getItem()) {
            mealStack.decrement(mealCount);
            outputStack.increment(mealCount);
        }
    }

    private void useStoredContainersOnMeal() {
        ItemStack mealStack = inventory.getStack(DRINK_DISPLAY_SLOT);
        ItemStack containerInputStack = inventory.getStack(CONTAINER_SLOT);
        ItemStack outputStack = inventory.getStack(OUTPUT_SLOT);

        if (isContainerValid(containerInputStack) && outputStack.getCount() < outputStack.getMaxCount()) {
            int smallerStackCount = Math.min(mealStack.getCount(), containerInputStack.getCount());
            int mealCount = Math.min(smallerStackCount, mealStack.getMaxCount() - outputStack.getCount());
            if (outputStack.isEmpty()) {
                containerInputStack.decrement(mealCount);
                inventory.setStack(OUTPUT_SLOT, mealStack.split(mealCount));
            } else if (outputStack.getItem() == mealStack.getItem()) {
                mealStack.decrement(mealCount);
                containerInputStack.decrement(mealCount);
                outputStack.increment(mealCount);
            }
        }
    }

    public ItemStack useHeldItemOnMeal(ItemStack container) {
        if (isContainerValid(container) && !getDrink().isEmpty()) {
            container.decrement(1);
            return getDrink().split(1);
        }
        return ItemStack.EMPTY;
    }

    private boolean doesDrinkHaveContainer(ItemStack meal) {
        return !drinkContainerStack.isEmpty() || meal.getItem().hasRecipeRemainder();
    }

    public boolean isContainerValid(ItemStack containerItem) {
        if (containerItem.isEmpty()) {
            return false;
        } else {
            return !this.drinkContainerStack.isEmpty() ? this.drinkContainerStack.isItemEqual(containerItem) : this.getDrink().getRecipeRemainder().isItemEqual(containerItem);
        }
    }

    @Override
    public Text getName() {
        return customName != null ? customName : BCTextUtils.getTranslation("container.keg");
    }

    @Override
    public Text getDisplayName() {
        return getName();
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return customName;
    }

    public void setCustomName(Text name) {
        customName = name;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new KegBlockScreenHandler(syncId, inv, this, kegData);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return writeItems(new NbtCompound());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onInventorySlotChanged(int slot) {
                if (slot >= 0 && slot < DRINK_DISPLAY_SLOT) {
                    checkNewRecipe = true;
                }
                inventoryChanged();
            }
        };
    }

    private PropertyDelegate createIntArray() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> KegBlockEntity.this.fermentTime;
                    case 1 -> KegBlockEntity.this.fermentTimeTotal;
                    case 2 -> KegBlockEntity.this.kegTemperature;
                    case 3 -> KegBlockEntity.this.getTemperature();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> KegBlockEntity.this.fermentTime = value;
                    case 1 -> KegBlockEntity.this.fermentTimeTotal = value;
                    case 2 -> KegBlockEntity.this.kegTemperature = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
