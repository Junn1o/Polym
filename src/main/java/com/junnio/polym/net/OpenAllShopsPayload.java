package com.junnio.polym.net;

import com.junnio.polym.Polym;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenAllShopsPayload() implements CustomPacketPayload {
    public static final Type<OpenAllShopsPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Polym.MOD_ID, "open_all_shops")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenAllShopsPayload> CODEC =
            StreamCodec.unit(new OpenAllShopsPayload());
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

