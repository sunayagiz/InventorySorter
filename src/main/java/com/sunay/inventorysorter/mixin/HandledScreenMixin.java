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

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import com.sunay.inventorysorter.SortingMode;

import net.minecraft.client.MinecraftClient;

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
        // Custom Button with Cool S Icon and Tooltip
        ButtonWidget iconButton = new ButtonWidget(this.x + this.backgroundWidth - 16, this.y + 4, 12, 12, Text.empty(), button -> {
            if (Screen.hasShiftDown() || Screen.hasControlDown()) {
                InventorySorterClient.cycleMode();
                updateSortButtonTooltip(button);
            } else {
                this.sortActiveInventory();
            }
        }, (textSupplier) -> Text.translatable("gui.inventorysorter.sort_button.narration")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                // Dynamically update position to handle Recipe Book shifts or window resizing
                this.setX(calculateDynamicX(this));
                this.setY(y + 4);
                
                super.renderWidget(context, mouseX, mouseY, delta);
                // Draw the Cool S icon (8x8 centered in 12x12)
                context.drawTexture(SORT_ICON, this.getX() + 2, this.getY() + 2, 0, 0, 8, 8, 8, 8);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (this.clicked(mouseX, mouseY) && button == 1) { // Right click
                    InventorySorterClient.cycleMode();
                    updateSortButtonTooltip(this);
                    if (MinecraftClient.getInstance() != null) {
                        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                    }
                    return true;
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
        
        updateSortButtonTooltip(iconButton);
        this.addDrawableChild(iconButton);
    }

    @Unique
    private void updateSortButtonTooltip(ButtonWidget button) {
        Text tooltipText = Text.translatable("gui.inventorysorter.sort_button.tooltip")
                .append("\n")
                .append(Text.translatable("gui.inventorysorter.current_mode").append(": "))
                .append(InventorySorterClient.getCurrentMode().getDisplayName());
        button.setTooltip(Tooltip.of(tooltipText));
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (InventorySorterClient.getSortKeyBinding().matchesKey(keyCode, scanCode)) {
            this.sortActiveInventory();
            cir.setReturnValue(true);
        }
    }

    @Unique
    private int calculateDynamicX(ButtonWidget self) {
        int preferredX = this.x + this.backgroundWidth - 16;
        int preferredY = this.y + 4;
        
        boolean collision = false;
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget widget && widget != self) {
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
        boolean sortPlayer = false;

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
            sortPlayer = true;
        } else {
            int totalSlots = this.handler.slots.size();
            if (totalSlots >= 36) {
                startSlot = totalSlots - 36;
                endSlot = totalSlots - 10;
            }
        }

        if (startSlot != -1 && endSlot != -1) {
            ClientPlayNetworking.send(new ModNetworking.SortPayload(startSlot, endSlot, sortPlayer, InventorySorterClient.getCurrentMode()));
            // Play a satisfying click sound
            if (this.client != null) {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }
    }
}
