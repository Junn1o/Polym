package com.junnio.polym.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.UUID;

public record ShopOfferViewData(ShopOfferData offer, UUID ownerUuid, String ownerName) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ShopOfferViewData> CODEC =
            new StreamCodec<>() {
                @Override
                public ShopOfferViewData decode(RegistryFriendlyByteBuf buf) {
                    ShopOfferData offer = ShopOfferData.CODEC.decode(buf);
                    UUID uuid = buf.readUUID();
                    String name = buf.readUtf(64);
                    return new ShopOfferViewData(offer, uuid, name);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, ShopOfferViewData value) {
                    ShopOfferData.CODEC.encode(buf, value.offer());
                    buf.writeUUID(value.ownerUuid());
                    buf.writeUtf(value.ownerName(), 64);
                }
            };
}


