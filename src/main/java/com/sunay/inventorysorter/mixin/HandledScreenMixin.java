package com.sunay.inventorysorter.mixin;

import com.sunay.inventorysorter.InventorySorterClient;
import com.sunay.inventorysorter.ModNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

    @Unique private static final Identifier SORT_ICON = Identifier.of("inventorysorter", "textures/gui/sort_button.png");

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addSortButton(CallbackInfo ci) {
        int buttonX = calculateDynamicX();
        int buttonY = this.y + 4;

        // Custom Button with Cool S Icon
        ButtonWidget sortButton = new ButtonWidget(buttonX, buttonY, 12, 12, Text.empty(), button -> {
            this.sortActiveInventory();
        }, (textSupplier) -> Text.translatable("gui.inventorysorter.sort_button")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                super.renderWidget(context, mouseX, mouseY, delta);
                // Draw the Cool S icon (8x8 centered in 12x12)
                context.drawTexture(SORT_ICON, this.getX() + 2, this.getY() + 2, 0, 0, 8, 8, 8, 8);
            }
        };

        this.addDrawableChild(sortButton);
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
        int preferredX = this.x + this.backgroundWidth - 16;
        int preferredY = this.y + 4;
        
        boolean collision = false;
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget widget) {
                if (widget.getX() >= preferredX && widget.getX() < preferredX + 12 &&
                    widget.getY() >= preferredY && widget.getY() < preferredY + 12) {
                    collision = true;
                    break;
                }
            }
        }
        
        return collision ? preferredX - 14 : preferredX;
    }

    @Unique
    private void sortActiveInventory() {
        if (this.handler == null) {
            return;
        }

        int startSlot = -1;
        int endSlot = -1;

        if ((Object)this instanceof InventoryScreen) {
            startSlot = 9;
            endSlot = 35;
        } else if ((Object)this instanceof CreativeInventoryScreen creativeScreen) {
            if (creativeScreen.isInventoryTabSelected()) {
                startSlot = 9;
                endSlot = 35;
            }
        } else if (this.handler instanceof GenericContainerScreenHandler containerHandler) {
            startSlot = 0;
            endSlot = containerHandler.getRows() * 9 - 1;
        } else {
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
