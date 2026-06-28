package com.billuglow.mixin;

import com.billuglow.GlowManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EntityRenderDispatcher#hasOutline(Entity) decides whether an entity gets the
 * glow outline pass at all (normally true for vanilla Glowing effect / spectator
 * targets). We force it true for anyone in our glow list.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
	private void billuglow$hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof PlayerEntity player && GlowManager.getColor(player.getUuid()) != null) {
			cir.setReturnValue(true);
		}
	}
}
