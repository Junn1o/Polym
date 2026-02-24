package com.junnio.polym.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;



public record ShopOpenData(List<ShopOfferData> offers) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ShopOpenData> CODEC =
            new StreamCodec<>() {
                @Override
                public ShopOpenData decode(RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    List<ShopOfferData> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(ShopOfferData.CODEC.decode(buf));
                    }
                    return new ShopOpenData(list);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, ShopOpenData data) {
                    buf.writeVarInt(data.offers().size());
                    for (ShopOfferData offer : data.offers()) {
                        ShopOfferData safe = new ShopOfferData(
                                offer.buyA() == null ? ItemStack.EMPTY : offer.buyA(),
                                offer.buyB() == null ? ItemStack.EMPTY : offer.buyB(),
                                offer.sell() == null ? ItemStack.EMPTY : offer.sell()
                        );
                        ShopOfferData.CODEC.encode(buf, safe);
                    }
                }
            };
}


