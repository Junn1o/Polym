package com.junnio.polym.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record AllShopOpenData(List<ShopOfferViewData> offers) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AllShopOpenData> CODEC =
            new StreamCodec<>() {
                @Override
                public AllShopOpenData decode(RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    List<ShopOfferViewData> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(ShopOfferViewData.CODEC.decode(buf));
                    }
                    return new AllShopOpenData(list);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, AllShopOpenData data) {
                    buf.writeVarInt(data.offers().size());
                    for (ShopOfferViewData view : data.offers()) {
                        ShopOfferViewData.CODEC.encode(buf, view);
                    }
                }
            };
}