package com.junnio.polym.item;

import com.junnio.polym.Polym;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.spongepowered.include.com.google.common.base.Function;

public class ModItems {
    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Polym.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {

    }
    public static Item COPPER_COIN = ModItems.register("copper_coin",Item::new, new Item.Settings().rarity(Rarity.COMMON));
    public static Item COPPER_COIN_PILE = ModItems.register("copper_coin_pile", Item::new, new Item.Settings().rarity(Rarity.COMMON));
    public static Item COPPER_BIG_COIN = ModItems.register("copper_big_coin", Item::new, new Item.Settings().rarity(Rarity.COMMON));
    public static Item SILVER_COIN = ModItems.register("silver_coin", Item::new, new Item.Settings().rarity(Rarity.UNCOMMON));
    public static Item SILVER_COIN_PILE = ModItems.register("silver_coin_pile", Item::new, new Item.Settings().rarity(Rarity.UNCOMMON));
    public static Item SILVER_BIG_COIN = ModItems.register("silver_big_coin", Item::new, new Item.Settings().rarity(Rarity.UNCOMMON));
    public static Item GOLD_COIN = ModItems.register("gold_coin", Item::new, new Item.Settings().rarity(Rarity.RARE));
    public static Item GOLD_COIN_PILE = ModItems.register("gold_coin_pile", Item::new, new Item.Settings().rarity(Rarity.RARE));
    public static Item GOLD_BIG_COIN = ModItems.register("gold_big_coin", Item::new, new Item.Settings().rarity(Rarity.RARE));
}
