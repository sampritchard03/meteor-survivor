package com.example.addon.tasks.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.example.addon.tasks.Task;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ItemTasks {
    public static final Map<Item, ItemTaskSupplier> singleItemTasks = new HashMap<>();

    public static final ItemTaskSupplier DIRT = register(Items.DIRT);
    public static final ItemTaskSupplier OAK_PLANKS = register(Items.OAK_PLANKS);
    public static final ItemTaskSupplier OAK_LOG = register(Items.OAK_LOG);
    public static final ItemTaskSupplier STICK = register(Items.STICK);
    public static final ItemTaskSupplier CRAFTING_TABLE = register(Items.CRAFTING_TABLE);
    public static final ItemTaskSupplier WOODEN_SWORD = register(Items.WOODEN_SWORD);
    public static final ItemTaskSupplier WOODEN_PICKAXE = register(Items.WOODEN_PICKAXE);
    public static final ItemTaskSupplier WOODEN_AXE = register(Items.WOODEN_AXE);
    public static final ItemTaskSupplier WOODEN_SHOVEL = register(Items.WOODEN_SHOVEL);

    public static final ItemTaskSupplier LOGS = new ItemTaskSupplier(List.of(Items.OAK_LOG, Items.BIRCH_LOG, Items.ACACIA_LOG, Items.CHERRY_LOG, Items.JUNGLE_LOG, Items.SPRUCE_LOG, Items.DARK_OAK_LOG, Items.MANGROVE_LOG, Items.PALE_OAK_LOG));
    public static final ItemTaskSupplier PLANKS = new ItemTaskSupplier(List.of(Items.OAK_PLANKS, Items.BIRCH_PLANKS, Items.ACACIA_PLANKS, Items.CHERRY_PLANKS, Items.JUNGLE_PLANKS, Items.SPRUCE_PLANKS, Items.DARK_OAK_PLANKS, Items.MANGROVE_PLANKS, Items.PALE_OAK_PLANKS));

    public static ItemTaskSupplier register(Item item) {
        ItemTaskSupplier ret = new ItemTaskSupplier(List.of(item));

        return register(item, ret);
    }

    public static ItemTaskSupplier register(Item item, ItemTaskSupplier taskSupplier) {
        singleItemTasks.put(item, taskSupplier);
        return taskSupplier;
    }
}
