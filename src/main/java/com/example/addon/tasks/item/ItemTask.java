package com.example.addon.tasks.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.example.addon.mod;
import com.example.addon.item.ItemData;
import com.example.addon.item.Recipe;
import com.example.addon.tasks.Priority;
import com.example.addon.tasks.Task;
import com.example.addon.tasks.basic.DigPosTask;
import com.example.addon.tasks.compound.ClosestTask;
import com.example.addon.tasks.inventory.CraftTask;
import com.example.addon.tasks.world.CollectBlocksTask;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public abstract class ItemTask extends Task {

    public final List<Item> items;
    public final CollectBlocksTask collectBlocks;
    public final CraftTask craft;

    public ItemTask(List<Item> _item) {
        super("item");

        items = _item;
        
        collectBlocks = new CollectBlocksTask(items);

        List<Recipe> recipes = new ArrayList<>();

        for (Item item : items) {
            for (Recipe recipe : ItemData.allData.get(item).recipes) {
                recipes.add(recipe);
            }
        }

        if (!recipes.stream().anyMatch(r -> !r.ingredients.isEmpty())) craft = null;

        else craft = new CraftTask(recipes) {

            @Override
            public int getCount() {
                return ItemTask.this.getCount();
            }
            
        };
    }

    abstract public int getCount();
    

    @Override
    public boolean isFinished() {
        return mod.bot.inventory.getCount((i) -> items.contains(i.getItem())) >= getCount();
    }

    @Override
    public void onStart() {}

    @Override
    public boolean shouldSearch() {return false;}

    @Override
    public void search() {}

    @Override
    public Task onTick() {
        if (collectBlocks.shouldContinue()) return collectBlocks;
        if (craft != null && craft.canCraft() && craft.shouldContinue()) return craft;

        mod.log(""+!collectBlocks.isFinished()+", "+(craft == null ? "null" : craft.canCraft()+", "+!craft.isFinished()));

        if (!collectBlocks.isFinished()) return collectBlocks;
        if (craft != null && craft.canCraft() && !craft.isFinished()) return craft;
        
        return null;
    }

    @Override
    public int timeEstimate() {
        return collectBlocks.timeEstimate();
    }

    @Override
    public int priority() {
        return Priority.DEFAULT;
    }



    @Override
    public void onStop() {}

    @Override
    public boolean isEqual(Task o) {
        return false;
    }

    @Override
    public String debugInfo() {
        String ret = "[";
        boolean hasLength = false;
        for (Item item : items) {
            if (hasLength) ret += ", ";
            hasLength = true;
            ret += ""+item.getName(new ItemStack(item)).getString();
        }
        return ret+"] | "+getCount();
    }
}
