package com.junnio.mixin.client;

import com.junnio.polym.item.ItemGlow;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
//? if >=1.21.9 {
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} else {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.injection.At;
*///?}

//? if >=1.21.9 {
@Mixin(ItemEntityRenderer.class)
//?} else
/*@Mixin(LevelRenderer.class)*/
public class ItemEntityRendererMixin {

	//? if >=1.21.9 {
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void polym$outline(ItemEntity entity, ItemEntityRenderState state, float tickDelta, CallbackInfo ci) {
		ItemStack stack = entity.getItem();
		if (!ItemGlow.shouldGlow(stack)) return;
		if (!polym$hasClearLineOfSight(entity, tickDelta)) return;

		state.outlineColor = colorFor(stack);
	}
	//?} else {
	/*// before 1.21.9 the outline is decided by LevelRenderer, not the entity render state
	@WrapOperation(method = {"collectVisibleEntities", "renderEntities"},
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean polym$glow(Minecraft mc, Entity entity, Operation<Boolean> original) {
		return original.call(mc, entity) || polym$coinGlows(entity);
	}

	@WrapOperation(method = "renderEntities",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"))
	private int polym$glowColor(Entity entity, Operation<Integer> original) {
		if (entity instanceof ItemEntity item && ItemGlow.shouldGlow(item.getItem())) {
			return colorFor(item.getItem());
		}
		return original.call(entity);
	}

	private static boolean polym$coinGlows(Entity entity) {
		if (!(entity instanceof ItemEntity item) || !ItemGlow.shouldGlow(item.getItem())) return false;
		float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
		return polym$hasClearLineOfSight(item, tickDelta);
	}
	*///?}

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
