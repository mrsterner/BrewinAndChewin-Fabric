package dev.sterner.brewinandchewin.common.block.entity;

import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemHandler;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackHandler;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.mixin.accessors.RecipeManagerAccessorMixin;
import dev.sterner.brewinandchewin.common.block.KegBlock;
import dev.sterner.brewinandchewin.common.block.screen.KegBlockScreenHandler;
import dev.sterner.brewinandchewin.common.recipe.KegRecipe;
import dev.sterner.brewinandchewin.common.registry.BCBlockEntityTypes;
import dev.sterner.brewinandchewin.common.registry.BCRecipeTypes;
import dev.sterner.brewinandchewin.common.registry.BCTags;
import dev.sterner.brewinandchewin.common.util.BCTextUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
import java.util.Optional;

public class KegBlockEntity extends SyncedBlockEntity implements ExtendedScreenHandlerFactory, Nameable {
    public static final int MEAL_DISPLAY_SLOT = 5;
    public static final int CONTAINER_SLOT = 6;
    public static final int OUTPUT_SLOT = 7;
    public static final int INVENTORY_SIZE = OUTPUT_SLOT + 1;
    public static final int TEMPERATURE_LOOKUP_RANGE = 1;

    private final ItemStackHandler inventory;

    private int fermentTime;
    private int fermentTimeTotal;
    private ItemStack mealContainerStack;
    private Text customName;
    public int heat;
    public int cold;

    protected final PropertyDelegate kegData;
    private final Object2IntOpenHashMap<Identifier> experienceTracker;

    private Identifier lastRecipeID;
    private boolean checkNewRecipe;

    public KegBlockEntity( BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.KEG, pos, state);
        this.inventory = createHandler();
        this.mealContainerStack = ItemStack.EMPTY;
        this.kegData = createIntArray();
        this.experienceTracker = new Object2IntOpenHashMap<>();
        this.checkNewRecipe = true;
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        inventory.readNbt(compound.getCompound("Inventory"));
        fermentTime = compound.getInt("FermentTime");
        fermentTimeTotal = compound.getInt("FermentTimeTotal");
        mealContainerStack = ItemStack.fromNbt(compound.getCompound("Container"));
        if (compound.contains("CustomName", 8)) {
            customName = Text.Serializer.fromJson(compound.getString("CustomName"));
        }
        NbtCompound compoundRecipes = compound.getCompound("RecipesUsed");
        for (String key : compoundRecipes.getKeys()) {
            experienceTracker.put(new Identifier(key), compoundRecipes.getInt(key));
        }
        heat = compound.getInt("Heat");
        cold = compound.getInt("Cold");
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.putInt("FermentTime", fermentTime);
        compound.putInt("FermentTimeTotal", fermentTimeTotal);
        compound.put("Container", mealContainerStack.writeNbt(new NbtCompound()));
        if (customName != null) {
            compound.putString("CustomName", Text.Serializer.toJson(customName));
        }
        compound.put("Inventory", inventory.writeNbt(new NbtCompound()));
        NbtCompound compoundRecipes = new NbtCompound();
        experienceTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        compound.put("RecipesUsed", compoundRecipes);
        compound.putInt("Heat", heat);
        compound.putInt("Cold", cold);
    }

    private NbtCompound writeItems(NbtCompound compound) {
        writeNbt(compound);
        compound.put("Container", mealContainerStack.writeNbt(new NbtCompound()));
        compound.put("Inventory", inventory.writeNbt(new NbtCompound()));
        return compound;
    }

    public NbtCompound writeMeal(NbtCompound compound) {
        if (getMeal().isEmpty()) return compound;

        ItemStackHandler drops = new ItemStackHandler(INVENTORY_SIZE);
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.setStack(i, i == MEAL_DISPLAY_SLOT ? inventory.getStack(i) : ItemStack.EMPTY);
        }
        if (customName != null) {
            compound.putString("CustomName", Text.Serializer.toJson(customName));
        }
        compound.put("Container", mealContainerStack.writeNbt(new NbtCompound()));
        compound.put("Inventory", drops.writeNbt(new NbtCompound()));
        return compound;
    }

    // ======== BASIC FUNCTIONALITY ========

    public static void fermentingTick(World level, BlockPos pos, BlockState state, KegBlockEntity keg) {
        boolean didInventoryChange = false;
        keg.updateTemperature();
        if (keg.hasInput()) {
            Optional<KegRecipe> recipe = keg.getMatchingRecipe(new RecipeWrapper(keg.inventory));
            if (recipe.isPresent() && keg.canFerment(recipe.get())) {
                didInventoryChange = keg.processFermenting(recipe.get());
            } else {
                keg.fermentTime = 0;
            }
        } else if (keg.fermentTime > 0) {
            keg.fermentTime = MathHelper.clamp(keg.fermentTime - 2, 0, keg.fermentTimeTotal);
        }

        ItemStack mealStack = keg.getMeal();
        if (!mealStack.isEmpty()) {
            if (!keg.doesMealHaveContainer(mealStack)) {
                keg.moveMealToOutput();
                didInventoryChange = true;
            } else if (!keg.inventory.getStack(CONTAINER_SLOT).isEmpty()) {
                keg.useStoredContainersOnMeal();
                didInventoryChange = true;
            }
        }

        if (didInventoryChange) {
            keg.updateTemperature();
            keg.inventoryChanged();
        }
    }


    public static void animationTick(World world, BlockPos pos, BlockState state, KegBlockEntity keg) {
    }

    public void updateTemperature() {
        ArrayList<BlockState> states = new ArrayList<>();
        int range = TEMPERATURE_LOOKUP_RANGE;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    states.add(world.getBlockState(pos.add(x, y, z)));
                }
            }
        }
        heat = states.stream().filter(s -> s.isIn(BCTags.HOT_BLOCK)).mapToInt(s -> 1).sum();
        cold = states.stream().filter(s -> s.isIn(BCTags.COLD_BLOCK)).mapToInt(s -> 1).sum();

        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        world.markDirty(pos);
        world.updateComparators(pos, getCachedState().getBlock());
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
                if (recipe.getOutput().isItemEqual(getMeal())) {
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
        if (!mealContainerStack.isEmpty()) {
            return mealContainerStack;
        } else {
            return new ItemStack(getMeal().getItem().getRecipeRemainder());
        }
    }

    private boolean hasInput() {
        for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
            if (!inventory.getStack(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    protected boolean canFerment(KegRecipe recipe) {
        KegRecipe.Range i = recipe.getTemperature();
        int j = recipe.getTemperatureJei();
        if (hasInput() && (i.contains(heat - cold) || j == 3)) {
            ItemStack resultStack = recipe.getOutput();
            if (resultStack.isEmpty()) {
                return false;
            } else {
                ItemStack storedMealStack = inventory.getStack(MEAL_DISPLAY_SLOT);
                if (storedMealStack.isEmpty()) {
                    return true;
                } else if (!storedMealStack.isItemEqual(resultStack)) {
                    return false;
                } else if (storedMealStack.getCount() + resultStack.getCount() <= inventory.getMaxCountForSlot(MEAL_DISPLAY_SLOT)) {
                    return true;
                } else {
                    return storedMealStack.getCount() + resultStack.getCount() <= resultStack.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private boolean processFermenting(KegRecipe recipe) {
        if (world == null) return false;

        ++fermentTime;
        fermentTimeTotal = recipe.getfermentTime();
        if (fermentTime < fermentTimeTotal) {
            return false;
        }

        fermentTime = 0;
        mealContainerStack = recipe.getOutputContainer();
        ItemStack resultStack = recipe.getOutput();
        ItemStack storedMealStack = inventory.getStack(MEAL_DISPLAY_SLOT);
        if (storedMealStack.isEmpty()) {
            inventory.setStack(MEAL_DISPLAY_SLOT, resultStack.copy());
        } else if (storedMealStack.isItemEqual(resultStack)) {
            storedMealStack.increment(resultStack.getCount());
        }
        trackRecipeExperience(recipe);

        for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
            ItemStack slotStack = inventory.getStack(i);
            if (slotStack.getItem().hasRecipeRemainder() && world != null) {
                Direction direction = getCachedState().get(KegBlock.FACING).rotateYCounterclockwise();
                double dropX = pos.getX() + .5d + (direction.getOffsetX() * .25d);
                double dropY = pos.getY() + .7d;
                double dropZ = pos.getZ() + .5d + (direction.getOffsetZ() * .25d);
                ItemEntity entity = new ItemEntity(world, dropX, dropY, dropZ, new ItemStack(inventory.getStack(i).getItem()
                        .getRecipeRemainder()));
                entity.setVelocity(direction.getOffsetX() * .08f, .25f, direction.getOffsetZ() * .08f);
                world.spawnEntity(entity);
            }
            if (!slotStack.isEmpty())
                slotStack.decrement(1);
        }
        return true;
    }

    public void trackRecipeExperience(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier recipeID = recipe.getId();
            experienceTracker.addTo(recipeID, 1);
        }
    }

    public void clearUsedRecipes(PlayerEntity player) {
        grantStoredRecipeExperience(player.world, player.getPos());
        experienceTracker.clear();
    }

    public void grantStoredRecipeExperience(World world, Vec3d pos) {
        for (Object2IntMap.Entry<Identifier> entry : experienceTracker.object2IntEntrySet()) {
            world.getRecipeManager().get(entry.getKey()).ifPresent((recipe) -> splitAndSpawnExperience(world, pos, entry.getIntValue(), ((KegRecipe) recipe).getExperience()));
        }
    }

    private static void splitAndSpawnExperience(World world, Vec3d pos, int craftedAmount, float experience) {
        int expTotal = MathHelper.floor((float) craftedAmount * experience);
        float expFraction = MathHelper.fractionalPart((float) craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }

        while (expTotal > 0) {
            int expValue = ExperienceOrbEntity.roundToOrbSize(expTotal);
            expTotal -= expValue;
            world.spawnEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, expValue));
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ItemStack getMeal() {
        return inventory.getStack(MEAL_DISPLAY_SLOT);
    }

    public DefaultedList<ItemStack> getDroppableInventory() {
        DefaultedList<ItemStack> drops = DefaultedList.of();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            if (i != MEAL_DISPLAY_SLOT) {
                drops.add(inventory.getStack(i));
            }
        }
        return drops;
    }

    private void moveMealToOutput() {
        ItemStack mealStack = inventory.getStack(MEAL_DISPLAY_SLOT);
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
        ItemStack mealStack = inventory.getStack(MEAL_DISPLAY_SLOT);
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
        if (isContainerValid(container) && !getMeal().isEmpty()) {
            container.decrement(1);
            return getMeal().split(1);
        }
        return ItemStack.EMPTY;
    }

    private boolean doesMealHaveContainer(ItemStack meal) {
        return !mealContainerStack.isEmpty() || meal.getItem().hasRecipeRemainder();
    }

    public boolean isContainerValid(ItemStack containerItem) {
        if (containerItem.isEmpty()) return false;
        if (!mealContainerStack.isEmpty()) {
            return mealContainerStack.isItemEqual(containerItem);
        } else {
            return new ItemStack(getMeal().getItem().getRecipeRemainder()).isItemEqual(containerItem);
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
                if (slot >= 0 && slot < MEAL_DISPLAY_SLOT) {
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
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> KegBlockEntity.this.fermentTime = value;
                    case 1 -> KegBlockEntity.this.fermentTimeTotal = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
