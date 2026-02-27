package com.junnio.polym.item;

import net.minecraft.world.item.ItemStack;

public final class ItemGlow {
    public static boolean shouldGlow(ItemStack stack) {
        return stack.is(ModItems.COPPER_COIN)
                || stack.is(ModItems.COPPER_COIN_PILE)
                || stack.is(ModItems.COPPER_BIG_COIN)
                || stack.is(ModItems.SILVER_COIN)
                || stack.is(ModItems.SILVER_COIN_PILE)
                || stack.is(ModItems.SILVER_BIG_COIN)
                || stack.is(ModItems.GOLD_COIN)
                || stack.is(ModItems.GOLD_COIN_PILE)
                || stack.is(ModItems.GOLD_BIG_COIN)
                || stack.is(ModItems.DIAMOND_COIN)
                || stack.is(ModItems.DIAMOND_COIN_PILE)
                || stack.is(ModItems.DIAMOND_BIG_COIN);
    }
}

