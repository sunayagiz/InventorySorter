package com.sunay.inventorysorter;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier SORT_PACKET_ID = Identifier.of("inventory_sorter", "sort");
    private static final java.util.Map<java.util.UUID, Long> COOLDOWNS = new java.util.HashMap<>();

    public record SortPayload(int startSlot, int endSlot, boolean sortPlayer, SortingMode mode) implements CustomPayload {
        public static final CustomPayload.Id<SortPayload> ID = new CustomPayload.Id<>(SORT_PACKET_ID);
        public static final PacketCodec<RegistryByteBuf, SortPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.VAR_INT, SortPayload::startSlot,
                PacketCodecs.VAR_INT, SortPayload::endSlot,
                PacketCodecs.BOOL, SortPayload::sortPlayer,
                PacketCodecs.indexed(i -> SortingMode.values()[i], SortingMode::ordinal), SortPayload::mode,
                SortPayload::new
        );

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void registerC2SPackets() {
        PayloadTypeRegistry.playC2S().register(SortPayload.ID, SortPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SortPayload.ID, (payload, context) -> {
            java.util.UUID uuid = context.player().getUuid();
            long currentTime = System.currentTimeMillis();
            if (COOLDOWNS.containsKey(uuid) && currentTime - COOLDOWNS.get(uuid) < 500) {
                return;
            }
            COOLDOWNS.put(uuid, currentTime);
            context.player().getServer().execute(() -> {
                SortingLogic.sort(context.player(), context.player().currentScreenHandler, payload.startSlot(), payload.endSlot(), payload.mode());
                if (payload.sortPlayer()) {
                    int totalSlots = context.player().currentScreenHandler.slots.size();
                    if (totalSlots >= 36) {
                        SortingLogic.sort(context.player(), context.player().currentScreenHandler, totalSlots - 36, totalSlots - 10, payload.mode());
                    }
                }
            });
        });
    }
}
