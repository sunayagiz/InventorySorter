package com.sunay.inventorysorter;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventorySorter implements ModInitializer {
    public static final String MOD_ID = "inventorysorter";
    public static final Logger LOGGER = LoggerFactory.getLogger(InventorySorter.class);

    @Override
    public void onInitialize() {
        LOGGER.info("InventorySorter initialized! Sorting your world, one chest at a time.");
    }
}
