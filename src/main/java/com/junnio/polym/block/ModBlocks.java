package com.junnio.polym.block;

import com.junnio.polym.Polym;
import com.junnio.polym.sound.ModSounds;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.spongepowered.include.com.google.common.base.Function;

public class ModBlocks {
//    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
//        RegistryKey<Block> blockKey = keyOfBlock(name);
//        Block block = blockFactory.apply(settings.registryKey(blockKey));
//        if (shouldRegisterItem) {
//            RegistryKey<Item> itemKey = keyOfItem(name);
//            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
//            Registry.register(Registries.ITEM, itemKey, blockItem);
//        }
//        return Registry.register(Registries.BLOCK, blockKey, block);
//    }
//    private static RegistryKey<Block> keyOfBlock(String name) {
//        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Polym.MOD_ID, name));
//    }
//
//    private static RegistryKey<Item> keyOfItem(String name) {
//        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Polym.MOD_ID, name));
//    }
//    public static final Block Polym_Table = register(
//            "polym_table",
//            PolymTableBlock::new,
//            AbstractBlock.Settings
//                    .create()
//                    .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
//                    .requiresTool()
//                    .strength(1.5F)
//            ,
//            true
//    );
//
//    public static void initialize() {
//        ItemGroupEvents.modifyEntriesEvent(ModItems.CUSTOM_ITEM_GROUP_KEY).register((itemGroup) -> {
//            itemGroup.add(ModBlocks.Polym_Table.asItem());
//        });
//    }
    public static Block register(String name, Function<Block.Settings, Block> blockFactory, Block.Settings settings, Item.Settings itemSettings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Polym.MOD_ID, name));
        Block block = blockFactory.apply(settings.registryKey(blockKey));
        Registry.register(Registries.BLOCK, blockKey, block);

        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Polym.MOD_ID, name));
        BlockItem blockItem = new BlockItem(block, itemSettings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, blockItem);

        return block;
    }
    public static final Block POLYM_TABLE = ModBlocks.register(
            "polym_table",
            PolymTableBlock::new,
            AbstractBlock.Settings.create()
                    .sounds(ModSounds.POLYM_TABLE_SOUND_GROUP)
                    .strength(1.0F,3600000.0F)
                    .requiresTool()
                    .luminance((state) ->4)
                    .nonOpaque()
                    .solidBlock((state, world, pos) -> true)
                    .suffocates((state, world, pos) -> true)
                    .blockVision((state, world, pos) -> true)
            ,new Item.Settings().rarity(Rarity.EPIC)
    );
    public static void initialize() {
        Polym.LOGGER.info("Registering Custom Recipes for " + Polym.MOD_ID);
    }
}

