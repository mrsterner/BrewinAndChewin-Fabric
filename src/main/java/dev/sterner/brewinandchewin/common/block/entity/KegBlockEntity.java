package dev.sterner.brewinandchewin.common.block.entity;

import com.google.common.collect.Lists;
import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.RecipeManagerAccessorMixin;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import dev.sterner.brewinandchewin.common.block.KegBlock;
import dev.sterner.brewinandchewin.common.block.screen.KegBlockScreenHandler;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.registry.BCObjects;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import dev.sterner.brewinandchewin.common.registry.BCTags;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KegBlockEntity extends SyncedBlockEntity implements KegBlockInventory, ExtendedScreenHandlerFactory, Nameable {
    public static final int MEAL_DISPLAY_SLOT = 5;
    public static final int CONTAINER_SLOT = 7;
    public static final int OUTPUT_SLOT = 8;
    public static final int INVENTORY_SIZE = OUTPUT_SLOT + 1;

    private final DefaultedList<ItemStack> inventory;

    private int fermentTime;
    private int fermentTimeTotal;
    private ItemStack drinkContainerStack;
    private Text customName;
    public int kegTemperature;
    protected final PropertyDelegate kegData;
    private final Object2IntOpenHashMap<Identifier> usedRecipeTracker;
    private Identifier lastRecipeID;
    private boolean checkNewRecipe;

    public KegBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.KEG, pos, state);
        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.drinkContainerStack = ItemStack.EMPTY;
        this.kegData = new KegSyncedData();
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.checkNewRecipe = true;
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        readInventoryNbt(compound);
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
        writeInventoryNbt(compound);
        NbtCompound compoundRecipes = new NbtCompound();
        usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        compound.put("RecipesUsed", compoundRecipes);
    }

    public NbtCompound writeDrink(NbtCompound compound) {
        if (!this.getDrink().isEmpty()) {
            DefaultedList<ItemStack> drops = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

            for (int i = 0; i < INVENTORY_SIZE; ++i) {
                drops.set(i, i == MEAL_DISPLAY_SLOT ? getStack(i) : ItemStack.EMPTY);
            }

            if (this.customName != null) {
                compound.putString("CustomName", Text.Serializer.toJson(this.customName));
            }

            compound.put("Container", drinkContainerStack.writeNbt(new NbtCompound()));
            compound.put("Inventory", Inventories.writeNbt(new NbtCompound(), drops));
        }
        return compound;
    }



    // ======== BASIC FUNCTIONALITY ========

    public static void fermentingTick(World level, BlockPos pos, BlockState state, KegBlockEntity keg) {
        boolean didInventoryChange = false;
        keg.updateTemperature();
        if (keg.hasInput()) {
            Optional<KegRecipe> recipe = keg.getMatchingRecipe(new RecipeWrapper(keg));
            if (recipe.isPresent() && keg.canFerment(recipe.get())) {
                didInventoryChange = keg.processFermenting(recipe.get(), keg);
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
            } else if (!keg.getStack(6).isEmpty()) {
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

        ArrayList<BlockState> states = new ArrayList<>();
        int range = 1;

        int heat;
        int cold;
        for (heat = -range; heat <= range; ++heat) {
            for (int y = -range; y <= range; ++y) {
                for (cold = -range; cold <= range; ++cold) {
                    states.add(this.world.getBlockState(this.getPos().add(heat, y, cold)));
                }
            }
        }

        heat = states.stream()
                .filter(s -> s.isIn(TagsRegistry.HEAT_SOURCES))
                .filter(s -> s.contains(Properties.LIT))
                .filter(s -> s.get(Properties.LIT))
                .mapToInt(s -> 1)
                .sum();

        heat += states.stream()
                .filter(s -> s.isIn(TagsRegistry.HEAT_SOURCES))
                .filter(s -> !s.contains(Properties.LIT))
                .mapToInt(s -> 1)
                .sum();

        BlockState stateBelow = this.world.getBlockState(this.getPos().down());
        if (stateBelow.isIn(TagsRegistry.HEAT_CONDUCTORS)) {
            BlockState stateFurtherBelow = this.world.getBlockState(this.getPos().down(2));
            if (stateFurtherBelow.isIn(TagsRegistry.HEAT_SOURCES)) {
                if (stateFurtherBelow.contains(Properties.LIT)) {
                    if (stateFurtherBelow.get(Properties.LIT)) {
                        ++heat;
                    }
                } else {
                    ++heat;
                }
            }
        }

        cold = states.stream()
                .filter(s -> s.isIn(BCTags.FREEZE_SOURCES))
                .mapToInt((s) -> 1)
                .sum();

        float biomeTemperature = this.world.getBiome(this.getPos()).value().getTemperature();
        if (biomeTemperature <= 0.0F) {
            cold += 2;
        } else if (biomeTemperature == 2.0F) {
            heat += 2;
        }

        int fcTemp = 0;
        if (this.world.getBlockEntity(pos.down()) instanceof FermentationControllerBlockEntity blockEntity) {
            fcTemp = blockEntity.getTemperature();
        }
        this.kegTemperature = heat - cold + fcTemp;
    }

    public int getTemperature() {
        if (this.kegTemperature < -4 && this.kegTemperature > -9) {
            return 2;
        }
        if (this.kegTemperature < -8) {
            return 1;
        }
        if (this.kegTemperature > 4 && this.kegTemperature < 9) {
            return 4;
        }
        if (this.kegTemperature > 8) {
            return 5;
        }
        return 3;
    }

    public static void animationTick(World level, BlockPos pos, BlockState state, KegBlockEntity keg) {
    }

    private Optional<KegRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (world == null) return Optional.empty();

        if (lastRecipeID != null) {
            Recipe<RecipeWrapper> recipe = ((RecipeManagerAccessorMixin) world.getRecipeManager())
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
        for (int i = 0; i < 5; ++i) {
            if (!getStack(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onContentsChanged(int slot) {
        if (slot >= 0 && slot < 5) {
            this.checkNewRecipe = true;
        }

        this.inventoryChanged();
    }

    protected boolean canFerment(KegRecipe recipe) {
        int recipeTemp = recipe.getTemperature();
        if (this.hasInput() && (recipeTemp == 3 || recipeTemp == this.getTemperature())) {
            ItemStack resultStack = recipe.getOutput();
            if (resultStack.isEmpty()) {
                return false;
            } else {
                ItemStack fluidStack = this.getStack(4);
                ItemStack storedContainerStack = this.getStack(8);
                if (!storedContainerStack.isEmpty() && fluidStack.getItem().getRecipeRemainder() != null && !fluidStack.getItem().getRecipeRemainder().equals(storedContainerStack.getItem())) {
                    return false;
                } else if (storedContainerStack.getCount() >= storedContainerStack.getMaxCount()) {
                    return false;
                } else {
                    ItemStack storedDrinkStack = this.getStack(5);
                    if (storedDrinkStack.isEmpty()) {
                        return true;
                    } else if (!storedDrinkStack.isItemEqual(resultStack)) {
                        return false;
                    } else if (storedDrinkStack.getCount() + resultStack.getCount() <= this.getMaxCountForSlot(5)) {
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
                ItemStack storedMealStack = this.getStack(5);
                if (storedMealStack.isEmpty()) {
                    this.setStack(5, resultStack.copy());
                } else if (storedMealStack.isItemEqual(resultStack)) {
                    storedMealStack.increment(resultStack.getCount());
                }

                ItemStack storedContainers = this.getStack(8);
                ItemStack fluidStack = this.getStack(4);
                if (storedContainers.isEmpty()) {
                    this.setStack(8, fluidStack.copy().getRecipeRemainder());
                    if (fluidStack.getCount() == 1) {
                        this.setStack(4, ItemStack.EMPTY);
                    } else {
                        this.setStack(4, new ItemStack(fluidStack.getItem(), fluidStack.getCount() - 1));
                    }
                } else if (storedContainers.isItemEqual(this.getStack(4).getRecipeRemainder())) {
                    storedContainers.increment(resultStack.getCount());
                    if (fluidStack.getCount() == 1) {
                        this.setStack(4, ItemStack.EMPTY);
                    } else {
                        this.setStack(4, new ItemStack(fluidStack.getItem(), fluidStack.getCount() - 1));
                    }
                }

                keg.setLastRecipe(recipe);

                for (int i = 0; i < 4; ++i) {
                    ItemStack slotStack = this.getStack(i);
                    if (slotStack.getItem().hasRecipeRemainder()) {
                        Direction direction = this.getCachedState().get(KegBlock.FACING).rotateYCounterclockwise();
                        double x = (double) this.getPos().getX() + 0.5 + (double) direction.getOffsetX() * 0.25;
                        double y = (double) this.getPos().getY() + 0.7;
                        double z = (double) this.getPos().getZ() + 0.5 + (double) direction.getOffsetZ() * 0.25;

                        ItemEntity entity = new ItemEntity(world, x, y, z, this.getStack(i).getRecipeRemainder());
                        entity.setVelocity(((float) direction.getOffsetX() * 0.08F), 0.25, ((float) direction.getOffsetZ() * 0.08F));
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
        int expTotal = MathHelper.floor((float) craftedAmount * experience);
        float expFraction = MathHelper.fractionalPart((float) craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }

        ExperienceOrbEntity.spawn(level, pos, expTotal);
    }


    public ItemStack getDrink() {
        return this.getStack(5);
    }

    public DefaultedList<ItemStack> getDroppableInventory() {
        DefaultedList<ItemStack> drops = DefaultedList.of();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.add(i == MEAL_DISPLAY_SLOT ? ItemStack.EMPTY : getStack(i));
        }

        return drops;
    }

    private void moveDrinkToOutput() {
        ItemStack mealStack = getStack(5);
        ItemStack outputStack = getStack(7);
        int mealCount = Math.min(mealStack.getCount(), mealStack.getMaxCount() - outputStack.getCount());
        if (outputStack.isEmpty()) {
            setStack(7, mealStack.split(mealCount));
        } else if (outputStack.getItem() == mealStack.getItem()) {
            mealStack.decrement(mealCount);
            outputStack.increment(mealCount);
        }
    }

    private void useStoredContainersOnMeal() {
        ItemStack mealStack = getStack(5);
        ItemStack containerInputStack = getStack(6);
        ItemStack outputStack = getStack(7);

        if (isContainerValid(containerInputStack) && outputStack.getCount() < outputStack.getMaxCount()) {
            int smallerStackCount = Math.min(mealStack.getCount(), containerInputStack.getCount());
            int mealCount = Math.min(smallerStackCount, mealStack.getMaxCount() - outputStack.getCount());
            if (outputStack.isEmpty()) {
                containerInputStack.decrement(mealCount);
                setStack(7, mealStack.split(mealCount));
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
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);

        return nbt;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    private class KegSyncedData implements PropertyDelegate {

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
    }
}
