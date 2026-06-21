package com.betaworldmenu.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class BetaWorldMenuConfig {
    private static final File CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("beta_world_menu.json").toFile();
    private static BetaWorldMenuConfig instance;
    private static Gson gson;

    public int selectedWorldTypeID = 0;
    public Map<Integer, String> availableWorldTypes = Map.of(
            0, "Default",
            1, "Superflat",
            2, "Large Biomes",
            3, "AMPLIFIED"
    );
    public boolean showGameplayOptions = true;
    public static void init() {
        if (!BetaWorldMenuConfig.CONFIG_PATH.exists()) {
            gson = new GsonBuilder().setPrettyPrinting().create();
            instance = new BetaWorldMenuConfig();
            instance.write();
        }
    }

    public void write() {
        try (FileWriter writer = new FileWriter(BetaWorldMenuConfig.CONFIG_PATH)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not write BetaWorldMenu config file", e);
        }
    }

    public static BetaWorldMenuConfig get() {
        if (instance == null) {
            gson = new GsonBuilder().setPrettyPrinting().create();

            if (!BetaWorldMenuConfig.CONFIG_PATH.exists()) {
                instance = new BetaWorldMenuConfig();
                instance.write();
            } else {
                try (FileReader reader = new FileReader(BetaWorldMenuConfig.CONFIG_PATH)) {
                    instance = gson.fromJson(reader, BetaWorldMenuConfig.class);
                } catch (IOException e) {
                    throw new RuntimeException("Could not read BetaWorldMenu config file", e);
                }
            }
        }

        return instance;
    }
}
