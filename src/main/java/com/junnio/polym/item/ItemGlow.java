package com.junnio.polym.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class ItemGlow {
    private static final Set<Item> GLOWING = Set.of(
            ModItems.COPPER_COIN,
            ModItems.COPPER_COIN_PILE,
            ModItems.COPPER_BIG_COIN,
            ModItems.SILVER_COIN,
            ModItems.SILVER_COIN_PILE,
            ModItems.SILVER_BIG_COIN,
            ModItems.GOLD_COIN,
            ModItems.GOLD_COIN_PILE,
            ModItems.GOLD_BIG_COIN,
            ModItems.DIAMOND_COIN,
            ModItems.DIAMOND_COIN_PILE,
            ModItems.DIAMOND_BIG_COIN
    );

    public static boolean shouldGlow(ItemStack stack) {
        return GLOWING.contains(stack.getItem());
    }
}

