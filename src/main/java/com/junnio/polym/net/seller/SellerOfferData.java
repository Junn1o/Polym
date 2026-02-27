package com.junnio.polym.net.seller;

import com.junnio.polym.net.shop.ShopOfferData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;



public record SellerOfferData(List<ShopOfferData> offers) {
    public static final StreamCodec<RegistryFriendlyByteBuf, SellerOfferData> CODEC =
            new StreamCodec<>() {
                @Override
                public SellerOfferData decode(RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    List<ShopOfferData> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(ShopOfferData.CODEC.decode(buf));
                    }
                    return new SellerOfferData(list);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, SellerOfferData data) {
                    buf.writeVarInt(data.offers().size());
                    for (ShopOfferData offer : data.offers()) {
                        ShopOfferData safe = new ShopOfferData(
                                offer.buyA() == null ? ItemStack.EMPTY : offer.buyA(),
                                offer.buyB() == null ? ItemStack.EMPTY : offer.buyB(),
                                offer.buyC() == null ? ItemStack.EMPTY : offer.buyC(),
                                offer.sell() == null ? ItemStack.EMPTY : offer.sell(),
                                offer.sellB() == null ? ItemStack.EMPTY : offer.sellB()
                        );
                        ShopOfferData.CODEC.encode(buf, safe);
                    }
                }
            };
}


