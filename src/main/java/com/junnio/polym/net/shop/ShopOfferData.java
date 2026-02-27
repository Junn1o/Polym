package com.junnio.polym.net.shop;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ShopOfferData(ItemStack buyA, ItemStack buyB, ItemStack sell) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ShopOfferData> CODEC =
            new StreamCodec<>() {
                @Override
                public ShopOfferData decode(RegistryFriendlyByteBuf buf) {
                    ItemStack a = ItemStack.STREAM_CODEC.decode(buf);
                    boolean hasB = buf.readBoolean();
                    ItemStack b = hasB ? ItemStack.STREAM_CODEC.decode(buf) : ItemStack.EMPTY;
                    ItemStack s = ItemStack.STREAM_CODEC.decode(buf);
                    return new ShopOfferData(a, b, s);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, ShopOfferData value) {
                    ItemStack.STREAM_CODEC.encode(buf, value.buyA());

                    boolean hasB = value.buyB() != null && !value.buyB().isEmpty();
                    buf.writeBoolean(hasB);
                    if (hasB) ItemStack.STREAM_CODEC.encode(buf, value.buyB());

                    ItemStack.STREAM_CODEC.encode(buf, value.sell());
                }
            };
}


