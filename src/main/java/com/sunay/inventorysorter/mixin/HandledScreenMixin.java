package com.sunay.inventorysorter.mixin;

import com.sunay.inventorysorter.SortingLogic;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {
    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addSortButton(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("S"), button -> {
            if (this.client != null && this.client.player != null) {
                SortingLogic.sortInventory(this.client.player.getInventory());
            }
        }).dimensions(this.x + this.backgroundWidth - 24, this.y + 4, 20, 20).build());
    }
}
