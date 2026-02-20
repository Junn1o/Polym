package com.junnio.polym.net;

import com.junnio.polym.Polym;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record SaveSellerOffersPayload() implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "save_seller_offers");
    public static final Type<SaveSellerOffersPayload> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveSellerOffersPayload> CODEC
            = StreamCodec.unit(new SaveSellerOffersPayload());

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
