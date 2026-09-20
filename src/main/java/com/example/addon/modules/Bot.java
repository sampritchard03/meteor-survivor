package com.example.addon.modules;

import baritone.ex;
import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.pathing.goals.GoalBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.example.addon.AddonTemplate;
import com.example.addon.mod;
import com.example.addon.item.Recipe;
import com.example.addon.packets.PositionPacket;
import com.example.addon.packets.SpawnPacket;
import com.example.addon.packets.TickPacket;
import com.example.addon.tasks.Task;
import com.example.addon.tasks.TaskSuppliers;
import com.example.addon.tasks.entity.TargetEntityTask;
import com.example.addon.tasks.inventory.CraftTask;
import com.example.addon.tasks.item.ItemTask;
import com.example.addon.tasks.item.ItemTasks;
import com.example.addon.tasks.world.TargetBlockTask;
import com.example.addon.utils.Pair;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Bot extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Integer> example = sgGeneral.add(new IntSetting.Builder()
		.name("dirt amount")
		.description("Amount of dirt to mine.")
		.defaultValue(0)
		.range(0, 256)
        .sliderMax(256)
        .sliderMin(0)
		.build()
	);
    //public final Inventory inventory = new Inventory();

	private double x; private double y; private double z;
	private double dx; private double dy; private double dz;
	private int ix; private int iy; private int iz;
	private int t;
	

	public final Inventory inventory;
    public final String username = mc.getUser().getName();
	public final List<Pair<Predicate<Block>, TargetBlockTask>> targetBlockFilter = new ArrayList<>();
	public final List<Pair<Predicate<Entity>, TargetEntityTask>> targetEntityFilter = new ArrayList<>();
	public Task mainTask;
	public double x() {return x;}; public double y() {return y;}; public double z() {return z;}
	public double dx() {return dx;}; public double dy() {return dy;}; public double dz() {return dz;}
	public double ix() {return ix;}; public double iy() {return iy;}; public double iz() {return iz;}
	public Vec3 pos() {return new Vec3(x, y, z);};
	public Vec3 eyePos() {return new Vec3(x, y+mc.player.getEyeHeight(mc.player.getPose()), z);}
	public Vec3 vel() {return new Vec3(dx, dy, dz);};
	public BlockPos blockPos() {return new BlockPos(ix, iy, iz);}
	public float pitch() {return mc.player.getXRot();}
	public float yaw() {return mc.player.getYHeadRot();}

	public Bot() {
		super(AddonTemplate.BOTCATEGORY, "bot", "Pathfinds to the configured coordinates with Baritone.");
		inventory = new Inventory();

	}

	public void mcTick() {
		double nx = mc.player.getX(), ny = mc.player.getY(), nz = mc.player.getZ();
        dx = nx - x; dy = ny - y; dz = nz - z;
		x = nx; y = ny; z = nz;
		ix = (int)x; iy = (int)y; iz = (int)z;
    }

	private void findEntityTargets() {
        if (mod.mc.level == null || mod.mc.player == null) return;

        Vec3 origin = mod.mc.player.position();

        Map<TargetEntityTask, Entity> closestPerTask = new HashMap<>();
        Map<TargetEntityTask, Double> closestDistancePerTask = new HashMap<>();

        for (Entity entity : mod.mc.level.entitiesForRendering()) {
            if (entity == mod.mc.player) continue;

            double distance = entity.position().distanceToSqr(origin);

            for (Pair<Predicate<Entity>, TargetEntityTask> pair : targetEntityFilter) {
                if (!pair.left.test(entity)) continue;

                TargetEntityTask task = pair.right;
                Double taskDistance = closestDistancePerTask.get(task);
                if (taskDistance == null || distance < taskDistance) {
                    closestDistancePerTask.put(task, distance);
                    closestPerTask.put(task, entity);
                }

                break;
            }
        }

        for (Pair<Predicate<Entity>, TargetEntityTask> pair : targetEntityFilter) {
            pair.right.targetEntity = closestPerTask.get(pair.right);
        }
    }

	private void findBlockTargets() {
        if (mod.mc.level == null || mod.mc.player == null) return;

        BlockPos origin = mod.mc.player.blockPosition();

        Map<TargetBlockTask, BlockPos> closestPerTask = new HashMap<>();
        Map<TargetBlockTask, Double> closestDistancePerTask = new HashMap<>();

        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-16, -16, -16),
                origin.offset(16, 16, 16))
		) {
			Block block = mod.mc.level.getBlockState(pos).getBlock();
			double distance = pos.distSqr(origin);

			for (Pair<Predicate<Block>, TargetBlockTask> pair : targetBlockFilter) {
				if (!pair.left.test(block)) continue;

				TargetBlockTask task = pair.right;
				Double taskDistance = closestDistancePerTask.get(task);
				if (taskDistance == null || distance < taskDistance) {
					closestDistancePerTask.put(task, distance);
					closestPerTask.put(task, pos.immutable());
				}

				break;
            }
        }

		for (Pair<Predicate<Block>, TargetBlockTask> pair : targetBlockFilter) {
			pair.right.targetPos = closestPerTask.get(pair.right);
		}
    }

	public boolean isWithinRange(BlockPos pos, double range) {
        return mod.mc.player != null && mod.bot.eyePos().distanceTo(Vec3.atCenterOf(pos)) <= range;
    }

	@Override
	public void onActivate() {
		mainTask = ItemTasks.OAK_PLANKS.get(() -> example.get());
	}

	@Override
	public void onDeactivate() {
		mainTask = null;
	}
	
    @EventHandler
    private void onPlayerTick(TickEvent.Post event) {
        mcTick();
		mod.b.getInputOverrideHandler().clearAllKeys();
		if (t % 10 == 0) {
			findBlockTargets();
			findEntityTargets();
			mainTask.trySearch(); 
		}
        if (!mainTask.isFinished()) {
            mainTask.tick();
            mod.log(mainTask.getHierarchy());
        } else mainTask.stop();
		t++;
    }

	public class Inventory {
        public int getCount(Predicate<ItemStack> pred) {
            if (mc.player == null) return 0;
            Iterator<ItemStack> iterator = mc.player.getInventory().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                ItemStack stack = iterator.next();
                if (pred.test(stack)) i+=stack.count();
            }
            if (mc.player.containerMenu instanceof CraftingMenu menu) {
                for (Slot slot : menu.getInputGridSlots()) {
                    if (pred.test(slot.getItem())) i += slot.getItem().count();
                }
            }
            return i;
        }

        public boolean canCraft(Recipe recipe) {
            if (recipe == null) return false;
            for (Pair<List<Item>, Integer> ingredient : recipe.ingredients) {
                if (ingredient.left == null || ingredient.left.isEmpty() || ingredient.right <= 0) return false;
                if (getCount((i) -> ingredient.left.contains(i.getItem())) < ingredient.right)
                    return false;
            }
            return true;
        }

        // returns the fastest tool (or empty hand) for breaking the given block state
        public ItemStack getBestTool(BlockState state) {
            ItemStack best = ItemStack.EMPTY;
            float bestScore = toolScore(best, state);
            if (mc.player == null) return best;
            Iterator<ItemStack> iterator = mc.player.getInventory().iterator();
            while (iterator.hasNext()) {
                ItemStack stack = iterator.next();
                float score = toolScore(stack, state);
                if (score > bestScore) {
                    bestScore = score;
                    best = stack;
                }
            }
            return best;
        }

        private float toolScore(ItemStack stack, BlockState state) {
            float speed = stack.isEmpty() ? 1.0F : stack.getDestroySpeed(state);
            boolean correctTool = !stack.isEmpty() && stack.isCorrectToolForDrops(state);
            return speed / (correctTool ? 30F : 100F);
        }
    }
}
