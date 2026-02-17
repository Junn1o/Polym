package com.junnio.polym.net;

import com.junnio.polym.Polym;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenSellerPayLoad() implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "open_seller");
    public static final Type<OpenSellerPayLoad> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSellerPayLoad> CODEC =
            StreamCodec.unit(new OpenSellerPayLoad());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
