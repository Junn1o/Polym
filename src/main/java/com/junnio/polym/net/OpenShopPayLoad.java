package com.junnio.polym.net;

import com.junnio.polym.Polym;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenShopPayLoad() implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "open_shop");
    public static final Type<OpenShopPayLoad> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenShopPayLoad> CODEC =
            StreamCodec.unit(new OpenShopPayLoad());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

