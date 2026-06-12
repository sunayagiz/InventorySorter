package com.sunay.inventorysorter.mixin;

import com.sunay.inventorysorter.InventorySorterClient;
import com.sunay.inventorysorter.ModNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {
    @Shadow protected T handler;
    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addSortButton(CallbackInfo ci) {
        int buttonX = calculateDynamicX();
        int buttonY = this.y + 4;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.inventorysorter.sort_button"), button -> {
            this.sortActiveInventory();
        }).dimensions(buttonX, buttonY, 20, 20).build());
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (InventorySorterClient.getSortKeyBinding().matchesKey(keyCode, scanCode)) {
            this.sortActiveInventory();
            cir.setReturnValue(true);
        }
    }

    @Unique
    private int calculateDynamicX() {
        int preferredX = this.x + this.backgroundWidth - 24;
        int preferredY = this.y + 4;
        
        // Simple collision detection with other buttons to avoid overlap
        boolean collision = false;
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget widget) {
                if (widget.getX() >= preferredX && widget.getX() < preferredX + 20 &&
                    widget.getY() >= preferredY && widget.getY() < preferredY + 20) {
                    collision = true;
                    break;
                }
            }
        }
        
        // If there's a collision, move the button to the left
        return collision ? preferredX - 22 : preferredX;
    }

    @Unique
    private void sortActiveInventory() {
        int startSlot = -1;
        int endSlot = -1;

        // Detect which inventory is currently focused based on the screen type
        if ((Object)this instanceof InventoryScreen) {
            // Player inventory: sort main 27 slots (9-35)
            startSlot = 9;
            endSlot = 35;
        } else if (this.handler instanceof GenericContainerScreenHandler containerHandler) {
            // Chest inventory: sort all container slots
            startSlot = 0;
            endSlot = containerHandler.getRows() * 9 - 1;
        } else {
            // Fallback for other containers: sort slots that are not part of the player inventory
            // Player inventory is typically the last 36 slots
            int totalSlots = this.handler.slots.size();
            if (totalSlots > 36) {
                startSlot = 0;
                endSlot = totalSlots - 37;
            }
        }

        if (startSlot != -1 && endSlot != -1) {
            ClientPlayNetworking.send(new ModNetworking.SortPayload(startSlot, endSlot));
        }
    }
}
