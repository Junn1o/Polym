package com.junnio.polym.net.shop;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ShopOfferData(ItemStack buyA, ItemStack buyB, ItemStack buyC, ItemStack sell, ItemStack sellB) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ShopOfferData> CODEC =
            new StreamCodec<>() {
                @Override
                public ShopOfferData decode(RegistryFriendlyByteBuf buf) {
                    ItemStack a = ItemStack.STREAM_CODEC.decode(buf);

                    boolean hasB = buf.readBoolean();
                    ItemStack b = hasB ? ItemStack.STREAM_CODEC.decode(buf) : ItemStack.EMPTY;

                    boolean hasC = buf.readBoolean();
                    ItemStack c = hasC ? ItemStack.STREAM_CODEC.decode(buf) : ItemStack.EMPTY;

                    ItemStack s = ItemStack.STREAM_CODEC.decode(buf);

                    boolean hasSellB = buf.readBoolean();
                    ItemStack sb = hasSellB ? ItemStack.STREAM_CODEC.decode(buf) : ItemStack.EMPTY;

                    return new ShopOfferData(a, b, c, s, sb);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, ShopOfferData value) {
                    ItemStack.STREAM_CODEC.encode(buf, value.buyA());

                    boolean hasB = value.buyB() != null && !value.buyB().isEmpty();
                    buf.writeBoolean(hasB);
                    if (hasB) ItemStack.STREAM_CODEC.encode(buf, value.buyB());

                    boolean hasC = value.buyC() != null && !value.buyC().isEmpty();
                    buf.writeBoolean(hasC);
                    if (hasC) ItemStack.STREAM_CODEC.encode(buf, value.buyC());

                    ItemStack.STREAM_CODEC.encode(buf, value.sell());

                    boolean hasSellB = value.sellB() != null && !value.sellB().isEmpty();
                    buf.writeBoolean(hasSellB);
                    if (hasSellB) ItemStack.STREAM_CODEC.encode(buf, value.sellB());
                }
            };
}