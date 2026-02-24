package com.junnio.mixin.client;

import com.junnio.polym.item.ItemGlow;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void polym$outline(ItemEntity entity, ItemEntityRenderState state, float tickDelta, CallbackInfo ci) {
		ItemStack stack = entity.getItem();
		if (!ItemGlow.shouldGlow(stack)) return;
		int col = colorFor(stack);
		state.outlineColor = col;
	}

	private static int colorFor(ItemStack s) {
		return switch (s.getRarity()) {
			case COMMON   -> 0xFFB0B0B0;
			case UNCOMMON -> 0xFF55FF55;
			case RARE     -> 0xFF5555FF;
			case EPIC     -> 0xFFFF55FF;
		};
	}
}