package com.junnio.polycoin.item;

import com.junnio.polycoin.PoLymCoin;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.spongepowered.include.com.google.common.base.Function;

public class ModItems {
    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PoLymCoin.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {

    }
    public static Item COPPER_COIN = ModItems.register("copper_coin",Item::new, new Item.Settings());
    public static Item COPPER_COIN_PILE = ModItems.register("copper_coin_pile", Item::new, new Item.Settings());
    public static Item COPPER_BIG_COIN = ModItems.register("copper_big_coin", Item::new, new Item.Settings());
    public static Item SILVER_COIN = ModItems.register("silver_coin", Item::new, new Item.Settings());
    public static Item SILVER_COIN_PILE = ModItems.register("silver_coin_pile", Item::new, new Item.Settings());
    public static Item SILVER_BIG_COIN = ModItems.register("silver_big_coin", Item::new, new Item.Settings());
    public static Item GOLD_COIN = ModItems.register("gold_coin", Item::new, new Item.Settings());
    public static Item GOLD_COIN_PILE = ModItems.register("gold_coin_pile", Item::new, new Item.Settings());
    public static Item GOLD_BIG_COIN = ModItems.register("gold_big_coin", Item::new, new Item.Settings());
}
