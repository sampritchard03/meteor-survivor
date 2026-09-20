package com.example.addon.tasks;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.example.addon.tasks.Task;
import com.example.addon.tasks.basic.DigPosTask;
import com.example.addon.tasks.basic.LookAtTask;
import com.example.addon.tasks.basic.PathfindTask;

public class TaskSuppliers {
    /* 
    public static Map<String, Task.Supplier> suppliers = new HashMap<>();

    public static DigPosTask.Supplier DIGPOS = (DigPosTask.Supplier)register(DigPosTask.class);
    public static PathfindTask.Supplier PATHFIND = (PathfindTask.Supplier)register(PathfindTask.class);
    public static LookAtTask.Supplier LOOKAT = (LookAtTask.Supplier)register(LookAtTask.class);

    public static Task.Supplier register(Class<? extends Task> taskClass) {
        String shortName = taskClass.getSimpleName().replace("Task", "");
        shortName = Character.toLowerCase(shortName.charAt(0)) + shortName.substring(1);
        String className = taskClass.getName();
        try {
            Class<?> supplierClass = null;
            for (Class<?> nestedClass : taskClass.getDeclaredClasses()) {
                if (nestedClass.getSimpleName().equals("Supplier")) {
                    supplierClass = nestedClass;
                    break;
                }
            }

            if (supplierClass == null) {
                throw new NoSuchMethodException("No nested Supplier class found for: " + className);
            }

            Task.Supplier supplier = (Task.Supplier) supplierClass.getDeclaredConstructor().newInstance();
            supplier.name = shortName;
            suppliers.put(shortName, supplier);
            return supplier;
            
        } catch (NoSuchMethodException e) {
            System.err.println("No parameterless constructor found for: " + className);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Failed to instantiate class: " + e.getMessage());
        }
        return null;
    }*/
}
