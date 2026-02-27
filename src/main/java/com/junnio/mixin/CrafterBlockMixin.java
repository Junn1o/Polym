package com.junnio.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {

    @Inject(
            method = "dispenseFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void blockSpecificRecipe(
            BlockState state,
            ServerLevel world,
            BlockPos pos,
            CallbackInfo ci
    ) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CrafterBlockEntity crafter)) return;

        // Get the recipe being crafted
        CraftingInput input = crafter.asCraftInput();
        Optional<RecipeHolder<CraftingRecipe>> recipe = world.recipeAccess()
                .getRecipeFor(RecipeType.CRAFTING, input, world);

        if (recipe.isEmpty()) return;

        // Convert RegistryKey to Identifier
        Identifier recipeId = recipe.get().id().identifier();

        // Check if this is your specific recipe
        if (recipeId.getNamespace().equals("polym")) {
            world.levelEvent(1050, pos, 0);
            ci.cancel();
        }
    }
}