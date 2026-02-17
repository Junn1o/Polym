package com.junnio.polym.net;

import com.junnio.polym.Polym;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record OpenShopByOwnerPayload(UUID owner) implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "open_shop_by_owner");
    public static final Type<OpenShopByOwnerPayload> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenShopByOwnerPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public OpenShopByOwnerPayload decode(RegistryFriendlyByteBuf buf) {
                    long most = buf.readLong();
                    long least = buf.readLong();
                    return new OpenShopByOwnerPayload(new UUID(most, least));
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, OpenShopByOwnerPayload value) {
                    UUID u = value.owner();
                    buf.writeLong(u.getMostSignificantBits());
                    buf.writeLong(u.getLeastSignificantBits());
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}