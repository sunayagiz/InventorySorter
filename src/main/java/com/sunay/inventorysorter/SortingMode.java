package com.sunay.inventorysorter;

import net.minecraft.text.Text;

/**
 * Defines the available sorting algorithms for the mod.
 */
public enum SortingMode {
    ALPHABETICAL("alphabetical"),
    ID("id"),
    CATEGORY("category");

    private final String translationKey;

    SortingMode(String name) {
        this.translationKey = "gui.inventorysorter.mode." + name;
    }

    public Text getDisplayName() {
        return Text.translatable(translationKey);
    }
    
    public SortingMode next() {
        SortingMode[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
