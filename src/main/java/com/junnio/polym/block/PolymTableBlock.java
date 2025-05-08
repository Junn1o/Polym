package com.junnio.polym.block;
import com.junnio.polym.screen.PolymTableScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PolymTableBlock extends Block {
    private static final Text TITLE = Text.translatable("container.polym_table");

    public PolymTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    // Create the crafting and result inventories for this screen handler
                    CraftingInventory craftingInventory = new CraftingInventory(null, 3, 3); // 3x3 crafting grid
                    CraftingResultInventory resultInventory = new CraftingResultInventory();
                    return new PolymTableScreenHandler(syncId, inv);
                }

                @Override
                public Text getDisplayName() {
                    return TITLE;
                }
            });
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }
}
