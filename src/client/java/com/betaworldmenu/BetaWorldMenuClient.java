package com.betaworldmenu;

import com.betaworldmenu.config.BetaWorldMenuConfig;
import net.fabricmc.api.ClientModInitializer;

public class BetaWorldMenuClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BetaWorldMenuConfig.init();
	}
}