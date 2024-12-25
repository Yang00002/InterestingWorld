package org.yang.interestingworld.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffectInstance.class)
public class MixinStatusEffectInstance
{
	@Shadow
	@Final
	private RegistryEntry<StatusEffect> type;

	@Inject(method = "onEntityDamage", at = @At(value = "INVOKE", target =
			"Lnet/minecraft/entity/effect/StatusEffect;" + "onEntityDamage" + "(Lnet/minecraft/entity/LivingEntity;" +
			"ILnet/minecraft/entity/damage" + "/DamageSource;F)V", shift = At.Shift.AFTER))
	public void mixinOnEntityDamage(LivingEntity entity, DamageSource source, float amount, CallbackInfo ci)
	{
		type.value().updateOnEntityDamage(entity, source, amount, (StatusEffectInstance) (Object) this);
	}
}
