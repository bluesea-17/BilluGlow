package com.billuglow.mixin;

import com.billuglow.GlowManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Entity#getTeamColorValue() is what the renderer asks for when it paints the
 * glow outline (it's normally just the player's scoreboard team color).
 * We hijack it so a glowing target reports our chosen color instead.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
	private void billuglow$getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
		Entity self = (Entity) (Object) this;
		if (self instanceof PlayerEntity player) {
			Integer color = GlowManager.getColor(player.getUuid());
			if (color != null) {
				cir.setReturnValue(color);
			}
		}
	}
}
