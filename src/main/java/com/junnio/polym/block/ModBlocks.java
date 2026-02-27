package com.junnio.polym.block;

import com.junnio.polym.Polym;
import com.junnio.polym.sound.ModSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.include.com.google.common.base.Function;

public class ModBlocks {

    public static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, Block.Properties settings, Item.Properties itemSettings) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Polym.MOD_ID, name));
        Block block = blockFactory.apply(settings.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Polym.MOD_ID, name));
        BlockItem blockItem = new BlockItem(block, itemSettings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

        return block;
    }
    public static final Block POLYM_TABLE = ModBlocks.register(
            "polym_table",
            PolymTableBlock::new,
            BlockBehaviour.Properties.of()
                    .sound(ModSounds.POLYM_TABLE_SOUND_GROUP)
                    .strength(50.0F,1200.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel((state) ->4)
                    .isRedstoneConductor((state, world, pos) -> true)
                    .isSuffocating((state, world, pos) -> true)
                    .isViewBlocking((state, world, pos) -> true)
            ,new Item.Properties().rarity(Rarity.RARE)
    );
    public static void initialize() {
    }
}

