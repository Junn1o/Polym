package com.junnio.mixin;

import com.junnio.polym.Polym;
import net.minecraft.block.BlockState;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {

    @Inject(
            method = "craft",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void blockSpecificRecipe(
            BlockState state,
            ServerWorld world,
            BlockPos pos,
            CallbackInfo ci
    ) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CrafterBlockEntity crafter)) return;

        // Get the recipe being crafted
        CraftingRecipeInput input = crafter.createRecipeInput();
        Optional<RecipeEntry<CraftingRecipe>> recipe = world.getRecipeManager()
                .getFirstMatch(RecipeType.CRAFTING, input, world);

        if (recipe.isEmpty()) return;

        // Convert RegistryKey to Identifier
        Identifier recipeId = recipe.get().id().getValue();

        // Check if this is your specific recipe
        if (recipeId.getNamespace().equals("polym")) {
            world.syncWorldEvent(1050, pos, 0);
            ci.cancel();
        }
    }
}