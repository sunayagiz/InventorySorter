package com.sunay.inventorysorter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class InventorySorterClient implements ClientModInitializer {
    private static KeyBinding sortKeyBinding;

    @Override
    public void onInitializeClient() {
        sortKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.inventorysorter.sort",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.inventorysorter.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (sortKeyBinding.wasPressed()) {
                if (client.player != null) {
                    SortingLogic.sortInventory(client.player.getInventory());
                }
            }
        });
    }
}
