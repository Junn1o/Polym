package com.junnio.polym.net.seller;

import com.junnio.polym.Polym;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AddOfferFromSlotsPayload() implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "seller_add_offer");
    public static final Type<AddOfferFromSlotsPayload> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, AddOfferFromSlotsPayload> CODEC =
            StreamCodec.unit(new AddOfferFromSlotsPayload());

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}