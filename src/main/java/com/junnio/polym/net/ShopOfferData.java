package com.junnio.polym.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ShopOfferData(ItemStack buyA, ItemStack buyB, ItemStack sell) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ShopOfferData> CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC, ShopOfferData::buyA,
                    ItemStack.STREAM_CODEC, ShopOfferData::buyB,
                    ItemStack.STREAM_CODEC, ShopOfferData::sell,
                    ShopOfferData::new
            );
}

