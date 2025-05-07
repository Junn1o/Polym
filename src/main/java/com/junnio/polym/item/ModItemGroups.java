package com.junnio.polym.item;

import com.junnio.polym.Polym;
import com.junnio.polym.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.include.com.google.common.base.Function;

public class ModItemGroups {
    public static final ItemGroup CUSTOM_ITEM_GROUP = registerItemGroup("item_group", groupKey ->
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.GOLD_BIG_COIN))
                    .displayName(Text.translatable("itemGroup.polymgroup"))
                    .build()
    );
    public static ItemGroup registerItemGroup(String name, Function<RegistryKey<ItemGroup>, ItemGroup> groupFactory) {
        RegistryKey<ItemGroup> groupKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(Polym.MOD_ID, name));
        ItemGroup itemGroup = groupFactory.apply(groupKey);
        Registry.register(Registries.ITEM_GROUP, groupKey, itemGroup);
        return itemGroup;
    }
    public static void initialize(){
        ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(Polym.MOD_ID, "item_group")))
                .register(entries -> {
                    entries.add(ModItems.COPPER_COIN);
                    entries.add(ModItems.COPPER_COIN_PILE);
                    entries.add(ModItems.COPPER_BIG_COIN);
                    entries.add(ModItems.SILVER_COIN);
                    entries.add(ModItems.SILVER_COIN_PILE);
                    entries.add(ModItems.SILVER_BIG_COIN);
                    entries.add(ModItems.GOLD_COIN);
                    entries.add(ModItems.GOLD_COIN_PILE);
                    entries.add(ModItems.GOLD_BIG_COIN);
                    entries.add(ModBlocks.POLYM_TABLE);
                });
    }
}
