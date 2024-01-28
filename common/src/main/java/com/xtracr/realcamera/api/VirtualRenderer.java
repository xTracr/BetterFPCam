package com.xtracr.realcamera.api;

import com.xtracr.realcamera.RealCameraCore;
import com.xtracr.realcamera.config.ConfigFile;
import com.xtracr.realcamera.config.ModConfig;
import com.xtracr.realcamera.util.Flag;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @see CompatExample
 */
public class VirtualRenderer {
    public static final ModConfig config = ConfigFile.modConfig;

    private static final Map<String, BiPredicate<Float, MatrixStack>> functionProvider = new HashMap<>();

    /**
     * @param modid    {@code mandatory}
     * @param function {@code mandatory} turn to vanilla rendering if return true.
     *                 {@link CompatExample#virtualRender See example here}
     */
    public static void register(String modid, BiPredicate<Float, MatrixStack> function) {
        functionProvider.put(modid, function);
    }

    /**
     * @return the value of {@link com.xtracr.realcamera.config.ModConfig.Compats#modModelPart modModelPart}
     * option in the config
     */
    public static String getModelPartName() {
        return config.getModModelPartName();
    }

    /**
     * mixins.MixinPlayerEntityRenderer.realCamera$onSetModelPoseRETURN
     */
    public static boolean shouldDisableRender(String modelPartName) {
        ModConfig.Disable.optionalParts.add(modelPartName);
        return Flag.isRenderingClientPlayer && Flag.isRenderingWorld &&
                config.shouldDisableModelPart(modelPartName) && RealCameraCore.isActive();
    }

    public static boolean virtualRender(float tickDelta, MatrixStack matrixStack) {
        return functionProvider.get(config.getModelModID()).test(tickDelta, matrixStack);
    }

    public static String[] getModidList() {
        return functionProvider.keySet().toArray(new String[0]);
    }
}
