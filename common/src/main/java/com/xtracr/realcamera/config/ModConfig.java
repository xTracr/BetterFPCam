package com.xtracr.realcamera.config;

import com.xtracr.realcamera.RealCameraCore;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ModConfig {
    public static final double MIN_DOUBLE = -1.0;
    public static final double MAX_DOUBLE = 1.0;
    public boolean enabled = false;
    public boolean isClassic = false;
    public boolean dynamicCrosshair = false;
    public boolean renderModel = true;
    public double adjustStep = 0.01d;
    public Classic classic = new Classic();
    public Binding binding = new Binding();

    public void set(ModConfig modConfig) {
        enabled = modConfig.enabled;
        isClassic = modConfig.isClassic;
        dynamicCrosshair = modConfig.dynamicCrosshair;
        renderModel = modConfig.renderModel;
        adjustStep = modConfig.adjustStep;
        classic = modConfig.classic;
        binding = modConfig.binding;
    }

    public void clamp() {
        adjustStep = MathHelper.clamp(adjustStep, 0.0d, MAX_DOUBLE);
        classic.clamp();
        binding.clamp();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    public boolean isClassic() {
        return isClassic;
    }

    public void setClassic(boolean value) {
        isClassic = value;
    }

    public boolean isCrosshairDynamic() {
        return dynamicCrosshair;
    }

    public boolean isRendering() {
        return renderModel;
    }

    public void cycleAdjustMode() {
        if (isClassic) classic.adjustMode = classic.adjustMode.cycle();
        else binding.adjustOffset = !binding.adjustOffset;
    }

    // classic
    public double getClassicX() {
        return classic.cameraX * classic.scale;
    }

    public double getClassicY() {
        return classic.cameraY * classic.scale;
    }

    public double getClassicZ() {
        return classic.cameraZ * classic.scale;
    }

    public double getCenterX() {
        return classic.centerX * classic.scale;
    }

    public double getCenterY() {
        return classic.centerY * classic.scale;
    }

    public double getCenterZ() {
        return classic.centerZ * classic.scale;
    }

    public float getClassicPitch() {
        return classic.pitch;
    }

    public float getClassicYaw() {
        return classic.yaw;
    }

    public float getClassicRoll() {
        return classic.roll;
    }

    public void adjustOffsetX(int count) {
        if (isClassic) {
            switch (classic.adjustMode) {
                case CENTER -> classic.centerX += count * adjustStep;
                case ROTATION -> classic.roll += count * 100 * (float) adjustStep;
                default -> classic.cameraX += count * adjustStep;
            }
            classic.clamp();
        } else {
            BindingTarget target = RealCameraCore.currentTarget;
            if (target.isEmpty()) return;
            if (binding.adjustOffset) target.setOffsetX(target.offsetX + count * adjustStep);
            else target.setRoll(target.roll + count * 100 * (float) adjustStep);
        }
    }

    public void adjustOffsetY(int count) {
        if (isClassic) {
            switch (classic.adjustMode) {
                case CENTER -> classic.centerY += count * adjustStep;
                case ROTATION -> classic.yaw += count * 100 * (float) adjustStep;
                default -> classic.cameraY += count * adjustStep;
            }
            classic.clamp();
        } else {
            BindingTarget target = RealCameraCore.currentTarget;
            if (target.isEmpty()) return;
            if (binding.adjustOffset) target.setOffsetY(target.offsetY + count * adjustStep);
            else target.setYaw(target.yaw + count * 100 * (float) adjustStep);
        }
    }

    public void adjustOffsetZ(int count) {
        if (isClassic) {
            switch (classic.adjustMode) {
                case CENTER -> classic.centerZ += count * adjustStep;
                case ROTATION -> classic.pitch += count * 100 * (float) adjustStep;
                default -> classic.cameraZ += count * adjustStep;
            }
            classic.clamp();
        } else {
            BindingTarget target = RealCameraCore.currentTarget;
            if (target.isEmpty()) return;
            if (binding.adjustOffset) target.setOffsetZ(target.offsetZ + count * adjustStep);
            else target.setPitch(target.pitch + count * 100 * (float) adjustStep);
        }
    }

    // binding
    public boolean updateModelAgain() {
        return binding.updateModel;
    }

    public List<BindingTarget> getTargetList() {
        binding.clamp();
        return binding.targetList;
    }

    public void putTarget(BindingTarget target) {
        if (target.isEmpty()) return;
        IntStream.range(0, binding.targetList.size())
                .filter(i -> binding.targetList.get(i).name.equals(target.name))
                .findAny()
                .ifPresentOrElse(i -> binding.targetList.set(i, target), () -> binding.targetList.add(target));
        binding.targetList.sort(Comparator.comparingInt(t -> -t.priority));
    }

    public static class Classic {
        public AdjustMode adjustMode = AdjustMode.CAMERA;
        public double scale = 8.0;
        public double cameraX = -0.5;
        public double cameraY = 0.04;
        public double cameraZ = -0.15;
        public double centerX = 0.0;
        public double centerY = 0.0;
        public double centerZ = 0.0;
        public float pitch = 0.0f;
        public float yaw = 18.0f;
        public float roll = 0.0f;

        private void clamp() {
            if (adjustMode == null) adjustMode = AdjustMode.CAMERA;
            scale = MathHelper.clamp(scale, 0.0, 64.0);
            cameraX = MathHelper.clamp(cameraX, MIN_DOUBLE, MAX_DOUBLE);
            cameraY = MathHelper.clamp(cameraY, MIN_DOUBLE, MAX_DOUBLE);
            cameraZ = MathHelper.clamp(cameraZ, MIN_DOUBLE, MAX_DOUBLE);
            centerX = MathHelper.clamp(centerX, MIN_DOUBLE, MAX_DOUBLE);
            centerY = MathHelper.clamp(centerY, MIN_DOUBLE, MAX_DOUBLE);
            centerZ = MathHelper.clamp(centerZ, MIN_DOUBLE, MAX_DOUBLE);
            pitch = MathHelper.wrapDegrees(pitch);
            yaw = MathHelper.wrapDegrees(yaw);
            roll = MathHelper.wrapDegrees(roll);
        }

        public enum AdjustMode {
            CAMERA, CENTER, ROTATION;

            private static final AdjustMode[] VALUES = values();

            public AdjustMode cycle() {
                return VALUES[(ordinal() + 1) % VALUES.length];
            }
        }
    }

    public static class Binding {
        public boolean adjustOffset = true;
        public boolean updateModel = true;
        public List<BindingTarget> targetList = new ArrayList<>(BindingTarget.defaultTargets);

        private void clamp() {
            if (targetList == null || targetList.isEmpty()) targetList = new ArrayList<>(BindingTarget.defaultTargets);
        }
    }
}
