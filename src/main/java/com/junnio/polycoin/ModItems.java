package com.junnio.polycoin;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.spongepowered.include.com.google.common.base.Function;

public class ModItems {
    public static final Item COPPER_COIN = register("copper_coin", Item::new, new Item.Settings());
    public static final Item COPPER_COIN_PILE = register("copper_coin_pile", Item::new, new Item.Settings());
    public static final Item COPPER_BIG_COIN = register("copper_big_coin", Item::new, new Item.Settings());
    public static final Item SLIVER_COIN = register("sliver_coin", Item::new, new Item.Settings());
    public static final Item SLIVER_COIN_PILE = register("sliver_coin_pile", Item::new, new Item.Settings());
    public static final Item SLIVER_BIG_COIN = register("sliver_big_coin", Item::new, new Item.Settings());
    public static final Item GOLD_COIN = register("gold_coin", Item::new, new Item.Settings());
    public static final Item GOLD_COIN_PILE = register("gold_coin_pile", Item::new, new Item.Settings());
    public static final Item GOLD_BIG_COIN = register("gold_big_coin", Item::new, new Item.Settings());
    public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(PoLymCoin.MOD_ID, "item_group"));
    public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.GOLD_BIG_COIN))
            .displayName(Text.translatable("itemGroup.polycoingroupname"))
            .build();
    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PoLymCoin.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);
        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register((fabricItemGroupEntries) -> {
            fabricItemGroupEntries.add(ModItems.COPPER_COIN);
            fabricItemGroupEntries.add(ModItems.COPPER_COIN_PILE);
            fabricItemGroupEntries.add(ModItems.COPPER_BIG_COIN);
            fabricItemGroupEntries.add(ModItems.SLIVER_COIN);
            fabricItemGroupEntries.add(ModItems.SLIVER_COIN_PILE);
            fabricItemGroupEntries.add(ModItems.SLIVER_BIG_COIN);
            fabricItemGroupEntries.add(ModItems.GOLD_COIN);
            fabricItemGroupEntries.add(ModItems.GOLD_COIN_PILE);
            fabricItemGroupEntries.add(ModItems.GOLD_BIG_COIN);
        });
    }
}
