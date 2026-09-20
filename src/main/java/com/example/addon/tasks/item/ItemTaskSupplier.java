package com.example.addon.tasks.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.example.addon.mod;
import com.example.addon.item.ItemData;
import com.example.addon.tasks.Task;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ItemTaskSupplier {

    private final Function<Supplier<Integer>, Task> function;

    public ItemTaskSupplier(Item item) {
        this(List.of(item));
    }

    public ItemTaskSupplier(List<Item> _items) {
        this((count) -> new ItemTask(_items) {

            @Override
            public int getCount() {
                return count.get();
            }
            
        });
    }

    public ItemTaskSupplier(Function<Supplier<Integer>, Task> _function) {
        function = _function;
    }

    public Task get(Supplier<Integer> count) {
        return function.apply(count);
    }
}