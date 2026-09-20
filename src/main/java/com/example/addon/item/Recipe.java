package com.example.addon.item;

import java.util.ArrayList;
import java.util.List;

import com.example.addon.utils.Pair;

import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;

public class Recipe {
    public final List<Pair<List<Item>, Integer>> ingredients;
    public final Item result;
    public final int resultCount;
    public final boolean requiresTable;

    public Recipe(Item _result, int _resultCount, List<Pair<List<Item>, Integer>> _ingredients, boolean _requiresTable) {
        result = _result; resultCount = _resultCount; ingredients = _ingredients; requiresTable = _requiresTable;
    }

    public List<Item> ingredientItems() {

        List<Item> ret = new ArrayList<>();

        for (Pair<List<Item>, Integer> pair : ingredients) {
            for (Item item : pair.left) {
                if (!ret.contains(item)) ret.add(item);
            }
        }

        return ret;
    }
}
