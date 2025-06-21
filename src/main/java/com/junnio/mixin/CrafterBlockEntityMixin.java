package com.junnio.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
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

@Mixin(CrafterBlockEntity.class)
public abstract class CrafterBlockEntityMixin {
    // Declare an accessor for the private field
    @Accessor("inputStacks")
    abstract DefaultedList<ItemStack> getInputStacks();

    @Inject(
            method = "setStack",
            at = @At("TAIL")
    )
    private void clearOutputAfterCraft(int slot, ItemStack stack, CallbackInfo ci) {
        if (slot >= 0 && slot < 9) {
            getInputStacks().set(slot, ItemStack.EMPTY); // Use the accessor
        }
    }
}