package com.betaworldmenu.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow
    protected abstract void clearChildren();

    @Shadow
    protected abstract void init();

    @Inject(method = "resize", at = @At("TAIL"))
    private void onResize(MinecraftClient client, int width, int height, CallbackInfo ci) {
        clearChildren();
        init();
    }
}
