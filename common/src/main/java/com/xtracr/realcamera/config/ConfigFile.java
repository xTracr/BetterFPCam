package com.xtracr.realcamera.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xtracr.realcamera.RealCamera;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigFile {
    private static final ModConfig modConfig = new ModConfig();
    private static final String FILE_NAME = RealCamera.MODID + ".json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH;

    static {
        File configDir = new File(Minecraft.getInstance().gameDirectory, "config");
        if (!configDir.exists()) configDir.mkdirs();
        PATH = configDir.toPath().resolve(FILE_NAME);
    }

    public static ModConfig config() {
        return modConfig;
    }

    public static void load() {
        try (BufferedReader reader = Files.newBufferedReader(PATH)) {
            modConfig.set(GSON.fromJson(reader, ModConfig.class));
            modConfig.clamp();
        } catch (Exception exception) {
            RealCamera.LOGGER.warn("Failed to load " + FILE_NAME);
            save();
        }
    }

    public static void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(PATH)) {
            GSON.toJson(modConfig, writer);
        } catch (Exception exception) {
            RealCamera.LOGGER.warn("Failed to save " + FILE_NAME, exception);
            reset();
        }
    }

    public static void reset() {
        try (BufferedWriter writer = Files.newBufferedWriter(PATH)) {
            modConfig.set(new ModConfig());
            GSON.toJson(modConfig, writer);
        } catch (Exception exception) {
            RealCamera.LOGGER.warn("Failed to reset " + FILE_NAME, exception);
        }
    }
}
