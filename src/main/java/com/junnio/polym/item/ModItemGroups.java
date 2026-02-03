package com.junnio.polym.item;

import com.junnio.polym.Polym;
import com.junnio.polym.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.include.com.google.common.base.Function;

public class ModItemGroups {
    public static final CreativeModeTab CUSTOM_ITEM_GROUP = registerItemGroup("item_group", groupKey ->
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.GOLD_BIG_COIN))
                    .title(Component.translatable("itemGroup.polymgroup"))
                    .build()
    );
    public static CreativeModeTab registerItemGroup(String name, Function<ResourceKey<CreativeModeTab>, CreativeModeTab> groupFactory) {
        ResourceKey<CreativeModeTab> groupKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Polym.MOD_ID, name));
        CreativeModeTab itemGroup = groupFactory.apply(groupKey);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, groupKey, itemGroup);
        return itemGroup;
    }
    public static void initialize(){
        ItemGroupEvents.modifyEntriesEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Polym.MOD_ID, "item_group")))
                .register(entries -> {
                    entries.accept(ModItems.COPPER_COIN);
                    entries.accept(ModItems.COPPER_COIN_PILE);
                    entries.accept(ModItems.COPPER_BIG_COIN);
                    entries.accept(ModItems.SILVER_COIN);
                    entries.accept(ModItems.SILVER_COIN_PILE);
                    entries.accept(ModItems.SILVER_BIG_COIN);
                    entries.accept(ModItems.GOLD_COIN);
                    entries.accept(ModItems.GOLD_COIN_PILE);
                    entries.accept(ModItems.GOLD_BIG_COIN);
                    entries.accept(ModBlocks.POLYM_TABLE);
                });
    }
}
