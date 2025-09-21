package com.betaworldmenu.mixin.client;

import com.betaworldmenu.config.BetaWorldMenuConfig;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VideoOptionsScreen.class)
public class VideoOptionsScreenMixin {

    @Inject(method = "getOptions", at = @At("HEAD"), cancellable = true)
    private static void onGetOptions(GameOptions gameOptions, CallbackInfoReturnable<SimpleOption<?>[]> cir)
    {
        if(!BetaWorldMenuConfig.get().oldVideoOptionsScreen)
            return;

        cir.setReturnValue(new SimpleOption[]{gameOptions.getGraphicsMode(), gameOptions.getViewDistance(), gameOptions.getAo(), gameOptions.getSimulationDistance(), gameOptions.getEnableVsync(), gameOptions.getMaxFps(), gameOptions.getGuiScale(), gameOptions.getBobView(), gameOptions.getMipmapLevels(), gameOptions.getCloudRenderMode(), gameOptions.getGamma(),  gameOptions.getFullscreen(), gameOptions.getDistortionEffectScale(), gameOptions.getParticles()});
        cir.cancel();
    }
}
