package com.betaworldmenu.mixin.client;

import com.betaworldmenu.betaworldmenu.Constants;
import com.betaworldmenu.config.BetaWorldMenuConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.betaworldmenu.betaworldmenu.Constants.*;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Shadow
    protected abstract void createLevel();

    @Shadow
    @Final
    WorldCreator worldCreator;

    @Unique private boolean isWorldOptionsToggled;

    @Unique private TextFieldWidget worldName;
    @Unique private Text worldDirectoryName;

    @Unique private TextFieldWidget worldSeed;

    @Unique private CyclingButtonWidget<WorldCreator.Mode> gameModeButton;

    @Unique private CyclingButtonWidget<Difficulty> difficultyButton;
    @Unique private CyclingButtonWidget<Boolean> allowCheatsButton;

    @Unique private ButtonWidget createNewWorldButton;
    @Unique private ButtonWidget cancelButton;

    @Unique private int halfWidth;

    protected CreateWorldScreenMixin(Text title) {
        super(title);

        this.isWorldOptionsToggled = false;
    }

    @Unique
    private static int clamp(int val, int max) {
        return Math.max(0, Math.min(max, val));
    }

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    public void betaworldmenu$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.halfWidth = this.width / 2;

        renderBackground(context);

        int padding = 58;
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.halfWidth, 20, -1);

        int textPositionX = this.halfWidth - 100;
        context.drawTextWithShadow(this.textRenderer, WORLD_NAME_LABEL, textPositionX, 47, Constants.getTextColor());
        context.drawTextWithShadow(this.textRenderer, this.worldDirectoryName, textPositionX, 85, Constants.getTextColor());

        context.drawTextWithShadow(this.textRenderer, SEED_LABEL, textPositionX, 47 + padding, Constants.getTextColor());
        context.drawTextWithShadow(this.textRenderer, SEED_INFO_LABEL, textPositionX, 85 + padding, Constants.getTextColor());

        ci.cancel();
    }

    @Inject(
            method = "init",
            at = @At("HEAD"),
            cancellable = true
    )
    public void betaworldmenu$init(CallbackInfo ci) {

        this.remove(worldName);
        this.remove(worldSeed);
        this.remove(gameModeButton);
        this.remove(difficultyButton);
        this.remove(allowCheatsButton);
        this.remove(createNewWorldButton);
        this.remove(cancelButton);

        this.halfWidth = this.width / 2;

        int centerX = this.halfWidth - 100;

        int rightColumnX = centerX + BUTTON_WIDTH/2 + 2;

        int padding = 23;
        int absY = 23 * 4;
        int textPadding = 25 - 5;

        this.worldName = new TextFieldWidget(this.textRenderer, centerX, 60, 200, 20, WORLD_NAME_LABEL);
        this.worldName.setText(this.worldCreator.getWorldName());
        this.worldName.setChangedListener(this::setWorldName);

        this.worldSeed = new TextFieldWidget(this.textRenderer, centerX, 60 + textPadding * 2 + 17, 200, 20, SEED_LABEL);
        this.worldSeed.setText(this.worldCreator.getSeed());
        this.worldSeed.setChangedListener(this::setSeed);


        //gamemodeButton
        this.gameModeButton = CyclingButtonWidget.<WorldCreator.Mode>builder(value -> value.name)
                .values(List.of(
                        WorldCreator.Mode.SURVIVAL,
                        WorldCreator.Mode.HARDCORE,
                        WorldCreator.Mode.CREATIVE
                ))
                .initially(this.worldCreator.getGameMode())
                .build(centerX, padding * 3 + absY, BUTTON_WIDTH / 2 - 2, BUTTON_HEIGHT, GAME_MODE_LABEL, (button, gameMode) ->
                    setGameMode(gameMode)
                );
        this.worldCreator.addListener(creator -> {
            this.gameModeButton.setValue(this.worldCreator.getGameMode());
            this.gameModeButton.active = !this.worldCreator.isDebug();
        });

        //allowCheatsButton
        this.allowCheatsButton = CyclingButtonWidget.onOffBuilder(this.worldCreator.areCheatsEnabled())
                .build(rightColumnX, padding * 3 + absY, BUTTON_WIDTH / 2 - 2, BUTTON_HEIGHT, ALLOW_CHEATS_TEXT, (button, allowCheats) ->
                    this.worldCreator.setCheatsEnabled(allowCheats)
                );
        this.worldCreator.addListener(creator -> {
            this.allowCheatsButton.setValue(this.worldCreator.areCheatsEnabled());
            this.allowCheatsButton.active = !this.worldCreator.isDebug() && !this.worldCreator.isHardcore();
        });

        //difficultyButton
        this.difficultyButton = CyclingButtonWidget.builder(Difficulty::getTranslatableName)
                .values(Difficulty.values())
                .initially(this.worldCreator.getDifficulty())
                .build(centerX, padding * 4 + absY, BUTTON_WIDTH, BUTTON_HEIGHT, DIFFICULTY_TEXT, (button, difficulty) ->
                    this.worldCreator.setDifficulty(difficulty)
                );
        this.worldCreator.addListener(creator -> {
            this.difficultyButton.setValue(this.worldCreator.getDifficulty());
            this.difficultyButton.active = !this.worldCreator.isHardcore();
        });


        //other Buttons
        createNewWorldButton = ButtonWidget.builder(CREATE_NEW_WORLD_TEXT, button -> createLevel())
                .dimensions(centerX, padding * 5 + absY, BUTTON_WIDTH / 2 - 2, BUTTON_HEIGHT)
                .build();

        cancelButton = ButtonWidget.builder(CANCEL_TEXT, button -> close())
                .dimensions(rightColumnX, padding * 5 + absY, BUTTON_WIDTH / 2 - 2, BUTTON_HEIGHT)
                .build();

        addDrawableChild(this.worldName);
        addDrawableChild(this.worldSeed);
        addDrawableChild(this.gameModeButton);
        addDrawableChild(this.difficultyButton);
        addDrawableChild(this.allowCheatsButton);

        addDrawableChild(createNewWorldButton);
        addDrawableChild(cancelButton);

        updateWorldOptionsVisibility();
        setInitialFocus(this.worldName);

        this.worldCreator.update();
        updateWorldDirectoryName();

//        this.worldCreator.setWorldType(WorldType.);
//
        System.out.println(worldCreator.getNormalWorldTypes());
//        System.out.println(worldCreator.getExtendedWorldTypes());
        List<WorldCreator.WorldType> worldTypes = worldCreator.getNormalWorldTypes();

        int configWorldTypeID = BetaWorldMenuConfig.get().worldTypeID;
        int safeID = clamp(configWorldTypeID, worldTypes.size());

        this.worldCreator.setWorldType(worldTypes.get(safeID));

        ci.cancel();
    }

    @Inject(
            method = "keyPressed",
            at = @At("HEAD"),
            cancellable = true
    )
    public void betaworldmenu$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {

        cir.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
        cir.cancel();
    }

    @Unique
    private void setWorldName(String newWorldName) {
        this.worldCreator.setWorldName(newWorldName);

        updateWorldDirectoryName();
    }

    @Unique
    private void setSeed(String newSeed) {
        this.worldCreator.setSeed(newSeed);
    }

    @Unique
    private void updateWorldDirectoryName() {
        this.worldDirectoryName = Text.empty()
                .append(WORLD_DIRECTORY_NAME_LABEL)
                .append(" ")
                .append(this.worldCreator.getWorldDirectoryName());
    }

    @Unique
    private void setGameMode(WorldCreator.Mode gameMode) {
        this.worldCreator.setGameMode(gameMode);
    }

    @Unique
    private void updateWorldOptionsVisibility() {
        setWorldOptionsVisibility(this.isWorldOptionsToggled);
    }

    @Unique
    private void setWorldOptionsVisibility(boolean visible) {
        this.isWorldOptionsToggled = visible;
        this.gameModeButton.visible = !visible;
        this.difficultyButton.visible = !visible;
        this.allowCheatsButton.visible = !visible;
        this.worldName.setVisible(!visible);
    }
}
