package com.junnio.mixin.client;

import com.junnio.polym.item.ItemGlow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
		if (!polym$hasClearLineOfSight(entity, tickDelta)) return;

		state.outlineColor = colorFor(stack);
	}

	private static boolean polym$hasClearLineOfSight(ItemEntity entity, float tickDelta) {
		Minecraft mc = Minecraft.getInstance();
		Entity camEntity = mc.getCameraEntity();
		if (camEntity == null || mc.level == null) return false;
		Vec3 from = camEntity.getEyePosition(tickDelta);
		Vec3 to = entity.getBoundingBox().getCenter();

		HitResult hit = mc.level.clip(new ClipContext(
				from, to,
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				camEntity
		));
		if (hit.getType() == HitResult.Type.MISS) return true;
		return hit.getLocation().distanceToSqr(from) >= to.distanceToSqr(from);
	}

	private static int colorFor(ItemStack s) {
		return switch (s.getRarity()) {
			case UNCOMMON -> 0xffdf7401;
			case COMMON   -> 0xffceecf5;
			case RARE     -> 0xfff7fe2e;
			case EPIC     -> 0xff00bfff;
		};
	}
}
