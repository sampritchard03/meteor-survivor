package com.example.addon.tasks.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import com.example.addon.mod;
import com.example.addon.item.Recipe;
import com.example.addon.modules.Bot;
import com.example.addon.tasks.Priority;
import com.example.addon.tasks.Task;
import com.example.addon.tasks.basic.PathfindTask;
import com.example.addon.tasks.compound.ClosestTask;
import com.example.addon.tasks.item.ItemTask;
import com.example.addon.tasks.world.TargetBlockTask;
import com.example.addon.utils.Pair;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalGetToBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public abstract class CraftTask extends TargetBlockTask implements ClosestTask {
    public final List<Recipe> recipes;
    public final List<Task> ingredientTasks;
    public final PathfindTask pathfind;
    private GoalGetToBlock goal;
    private int nextActionTick;

    public CraftTask(List<Recipe> _recipes) {
        super("craft");
        recipes = _recipes;
        ingredientTasks = new ArrayList<>();
        for (Recipe recipe : recipes) {
            for (int i = 0; i < recipe.ingredients.size(); i++) {
                Pair<List<Item>, Integer> ingredient = recipe.ingredients.get(i);
                ingredientTasks.add(new ItemTask(ingredient.left) {

                    @Override
                    public int getCount() {
                        int produced = mod.bot.inventory.getCount(stack -> stack.is(recipe.result));
                        int remaining = Math.max(0, CraftTask.this.getCount() - produced);
                        int craftsRequired = (remaining + recipe.resultCount - 1) / recipe.resultCount;
                        return craftsRequired * ingredient.right;
                    }
                    
                });
            }
        }
        pathfind = new PathfindTask() {
            @Override
            public Goal getGoal() {
                return goal;
            }
        };
    }

    abstract public int getCount();

    public boolean canCraft() {
        return getCraftableRecipe() != null;
    }

    private Recipe getCraftableRecipe() {
        return recipes.stream()
                .filter(java.util.Objects::nonNull)
                .filter(mod.bot.inventory::canCraft)
                .findFirst()
                .orElse(null);
    }

    @Override 
    public List<Task> getTasks() {return ingredientTasks;}

    @Override
    public void onStart() {}

    @Override
    public boolean shouldSearch() {return false;}

    @Override
    public void search() {}

    @Override
    public Task onTick() {
        if (mod.mc.player == null || mod.mc.level == null || mod.mc.gameMode == null) return null;
        if (!canCraft()) return null;

        Task closest = getClosest();
        if (closest != null) return closest;

        Recipe theRecipe = getCraftableRecipe();

        AbstractCraftingMenu menu;

        if (theRecipe.requiresTable) {
            if (!(mod.mc.player.containerMenu instanceof CraftingMenu _menu)) {
                if (targetPos == null) return null;
                if (!mod.bot.isWithinRange(targetPos, 4)) {
                    if (goal == null || !goal.getGoalPos().equals(targetPos)) goal = new GoalGetToBlock(targetPos);
                    return pathfind;
                }

                if (mod.mc.player.tickCount >= nextActionTick) {
                    BlockPos pos = targetPos;
                    mod.mc.gameMode.useItemOn(mod.mc.player, InteractionHand.MAIN_HAND,
                            new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
                    nextActionTick = mod.mc.player.tickCount + 10;
                }
                return null;
            } else
                menu = _menu;

        } else {
            if (!(mod.mc.player.containerMenu instanceof InventoryMenu)) {
                mod.mc.player.sendOpenInventory();
                return null;
            } else
                menu = mod.mc.player.inventoryMenu;
        }

        


        if (mod.mc.player.tickCount >= nextActionTick) {

            if (menu.getResultSlot().hasItem()) {
                mod.mc.gameMode.handleContainerInput(menu.containerId, CraftingMenu.RESULT_SLOT, 0,
                        ContainerInput.QUICK_MOVE, mod.mc.player);
                return null;
            }


            RecipeDisplayEntry recipe = findBookRecipe();
            mod.mc.gameMode.handlePlaceRecipe(menu.containerId, recipe.id(), false);
            nextActionTick = mod.mc.player.tickCount + 2;
        }
        return null;
    }

    private RecipeDisplayEntry findBookRecipe() {
        Recipe craftableRecipe = getCraftableRecipe();

        ContextMap context = SlotDisplayContext.fromLevel(mod.mc.level);
        return mod.mc.player.getRecipeBook().getCollections().stream()
                .flatMap(collection -> collection.getRecipes().stream())
                .filter(recipe -> recipe.display().result().resolveForStacks(context).stream()
                .anyMatch(stack -> stack.is(craftableRecipe.result)))
                //filter(recipe -> recipe.craftingRequirements().orElseGet(java.util.List::of).stream()
                //        .anyMatch(this::acceptsLog))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int timeEstimate() {
        return pathfind.isFinished() ? 10 : pathfind.timeEstimate() + 10;
    }

    @Override
    public int priority() {
        return Priority.DEFAULT;
    }

    @Override
    public boolean isFinished() {
        return hasTargetCount() && (!hasCraftingInputs());
    }

    private boolean hasTargetCount() {
        return mod.bot.inventory.getCount(stack -> recipes.stream().anyMatch((r) -> stack.is(r.result))) >= getCount();
    }

    private boolean hasCraftingInputs() {
        return mod.mc.player != null
                && mod.mc.player.containerMenu instanceof CraftingMenu menu
                && menu.getInputGridSlots().stream().anyMatch(Slot::hasItem);
    }

    @Override
    public void onStop() {
        goal = null;
    }

    @Override
    public boolean isEqual(Task o) {
        return false;
    }

    @Override
    public String debugInfo() {
        return "";
    }

    @Override
    public Predicate<Block> getPredicate() {
        return (b) -> b == Blocks.CRAFTING_TABLE;
    }
}
