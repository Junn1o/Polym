package com.junnio.polycoin.block;

import com.junnio.polycoin.PoLymCoin;
import com.junnio.polycoin.sound.ModSounds;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
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
//        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(PoLymCoin.MOD_ID, name));
//    }
//
//    private static RegistryKey<Item> keyOfItem(String name) {
//        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PoLymCoin.MOD_ID, name));
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
    public static Block register(String name, Function<Block.Settings, Block> blockFactory, Block.Settings settings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(PoLymCoin.MOD_ID, name));
        Block block = blockFactory.apply(settings.registryKey(blockKey));
        Registry.register(Registries.BLOCK, blockKey, block);

        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PoLymCoin.MOD_ID, name));
        BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, blockItem);

        return block;
    }
    public static final Block POLYM_TABLE = ModBlocks.register(
            "polym_table",
            PolymTableBlock::new,
            AbstractBlock.Settings.create()
                    .sounds(ModSounds.POLYM_TABLE_SOUND_GROUP)

                    .strength(1.5F)
                    .requiresTool()
    );
    public static void initialize() {
        PoLymCoin.LOGGER.info("Registering Custom Recipes for " + PoLymCoin.MOD_ID + POLYM_TABLE);
    }
}


