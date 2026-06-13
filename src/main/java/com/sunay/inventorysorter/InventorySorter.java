package com.sunay.inventorysorter;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main common entrypoint for the InventorySorter mod.
 * <p>
 * This class is responsible for initializing common features like networking,
 * which must be registered on both the client and the server.
 * </p>
 */
public class InventorySorter implements ModInitializer {
    /**
     * The unique identifier for the InventorySorter mod.
     */
    public static final String MOD_ID = "inventorysorter";
    public static final Logger LOGGER = LoggerFactory.getLogger(InventorySorter.class);

    /**
     * Called by the Fabric Loader when the mod is being initialized.
     * Registers the Client-to-Server (C2S) networking packets.
     */
    @Override
    public void onInitialize() {
        ModNetworking.registerC2SPackets();
        LOGGER.info("InventorySorter initialized! Sorting your world, one chest at a time.");
    }
}
