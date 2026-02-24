package com.junnio.polym.item;

import com.junnio.polym.Polym;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.spongepowered.include.com.google.common.base.Function;

public class ModItems {
    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Polym.MOD_ID, name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {

    }
    public static Item COPPER_COIN = ModItems.register("copper_coin",Item::new, new Item.Properties().rarity(Rarity.COMMON));
    public static Item COPPER_COIN_PILE = ModItems.register("copper_coin_pile", Item::new, new Item.Properties().rarity(Rarity.COMMON));
    public static Item COPPER_BIG_COIN = ModItems.register("copper_big_coin", Item::new, new Item.Properties().rarity(Rarity.COMMON));
    public static Item SILVER_COIN = ModItems.register("silver_coin", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static Item SILVER_COIN_PILE = ModItems.register("silver_coin_pile", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static Item SILVER_BIG_COIN = ModItems.register("silver_big_coin", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static Item GOLD_COIN = ModItems.register("gold_coin", Item::new, new Item.Properties().rarity(Rarity.RARE));
    public static Item GOLD_COIN_PILE = ModItems.register("gold_coin_pile", Item::new, new Item.Properties().rarity(Rarity.RARE));
    public static Item GOLD_BIG_COIN = ModItems.register("gold_big_coin", Item::new, new Item.Properties().rarity(Rarity.RARE));
}
