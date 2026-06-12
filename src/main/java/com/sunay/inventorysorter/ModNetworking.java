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

    public record SortPayload(int startSlot, int endSlot) implements CustomPayload {
        public static final CustomPayload.Id<SortPayload> ID = new CustomPayload.Id<>(SORT_PACKET_ID);
        public static final PacketCodec<RegistryByteBuf, SortPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.VAR_INT, SortPayload::startSlot,
                PacketCodecs.VAR_INT, SortPayload::endSlot,
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
            context.player().getServer().execute(() -> {
                SortingLogic.sort(context.player(), context.player().currentScreenHandler, payload.startSlot(), payload.endSlot());
            });
        });
    }
}
