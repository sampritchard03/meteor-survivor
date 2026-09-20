package com.example.addon.tasks.basic;

import com.example.addon.mod;
import com.example.addon.tasks.Priority;
import com.example.addon.tasks.Task;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public abstract class LookAtTask extends Task {
    private static final Minecraft mc = mod.mc;

    public LookAtTask() {
        super("lookAt");
    }

    public abstract Vec2 getRot();
    public abstract double getSpeed();

    @Override
    public void onStart() {}

    @Override
    public boolean shouldSearch() {return false;}

    @Override
    public void search() {}

    @Override
    public Task onTick() {
        Vec2 targetRot = getRot();

        float targetPitch = targetRot.x;
        float targetYaw = targetRot.y;

        float oldPitch = mc.player.getXRot();
        float oldYaw = mc.player.getYRot();

        float amount = Mth.clamp((float) getSpeed(), 0.0F, 1.0F);

        float pitchDelta = Mth.wrapDegrees(targetPitch - oldPitch);
        float yawDelta = Mth.wrapDegrees(targetYaw - oldYaw);

        float newPitch = oldPitch + pitchDelta * amount;
        float newYaw = oldYaw + yawDelta * amount;

        mc.player.setXRot(Mth.wrapDegrees(newPitch));
        mc.player.setYRot(Mth.wrapDegrees(newYaw));
        mc.player.setYHeadRot(Mth.wrapDegrees(newYaw));

        return null;
    }

    @Override
    public int timeEstimate() {
        return 0;
    }

    @Override
    public int priority() {
        return Priority.DEFAULT;
    }

    @Override
    public boolean isFinished() {
        Vec2 targetRot = getRot();
        float pitchDelta = Mth.wrapDegrees(targetRot.x - mc.player.getXRot());
        float yawDelta = Mth.wrapDegrees(targetRot.y - mc.player.getYRot());
        return Math.abs(pitchDelta) < 1 && Math.abs(yawDelta) < 10;
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isEqual(Task o) {
        return o instanceof LookAtTask l && l.getRot().equals(getRot()) && l.getSpeed() == getSpeed();
    }

    @Override
    public String debugInfo() {return "";}

    public static Vec2 getRotation(Vec3 target) {
        Vec3 from = mod.mc.player.getEyePosition();

        double xd = target.x - from.x;
        double yd = target.y - from.y;
        double zd = target.z - from.z;

        double sd = Math.sqrt(xd * xd + zd * zd);

        float targetPitch = (float) -(Mth.atan2(yd, sd) * 180.0F / Math.PI);
        float targetYaw = (float) (Mth.atan2(zd, xd) * 180.0F / Math.PI) - 90.0F;

        return new Vec2(targetPitch, targetYaw);
    }
}

