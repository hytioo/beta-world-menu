package com.betaworldmenu.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BetaWorldMenuConfigScreen implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::create;
    }

    private Screen create(Screen parentScreen) {
        BetaWorldMenuConfig config = BetaWorldMenuConfig.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setTitle(Text.translatable("title.betaworldmenu.config"));

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        //DynamicRegistryManager registryManager = server.getRegistryManager();
        //Registry<WorldPreset> presetRegistry = registryManager.get(RegistryKeys.WORLD_PRESET);

        Integer[] values = new Integer[config.availableWorldTypes.size()];
        for(int i = 0; i < values.length; i++)
        {
            values[i] = i;
        }

        general.addEntry(entryBuilder.startTextDescription(Text.of("Open world creation screen to update the list. Creating the world is unnecessary")).build());
        general.addEntry(
                entryBuilder.startSelector(
                                Text.of("World Type"),
                                values,
                                config.selectedWorldTypeID // current int value
                        )
                        .setDefaultValue(0)
                        .setNameProvider(i -> Text.of(config.availableWorldTypes.get(i)))
                        .setSaveConsumer(i -> config.selectedWorldTypeID = i)
                        .build()
        );

        general.addEntry(
                entryBuilder.startBooleanToggle(Text.of("Show Gameplay Options"), config.showGameplayOptions)
                        .setSaveConsumer(i -> config.showGameplayOptions = i)
                        .build()
        );

        builder.setSavingRunnable(config::write);

        return builder.build();
    }
}
