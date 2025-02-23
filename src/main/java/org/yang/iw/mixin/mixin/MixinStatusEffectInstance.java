package org.yang.iw.mixin.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.mixin.mixin_interface.InterfaceStatusEffectInstance;

@Mixin(StatusEffectInstance.class)
public class MixinStatusEffectInstance implements InterfaceStatusEffectInstance
{
	@Shadow
	@Final
	private RegistryEntry<StatusEffect> type;

	@Shadow
	private int duration;

	@Shadow
	private int amplifier;

	@Inject(method = "onEntityDamage", at = @At(value = "INVOKE", target =
			"Lnet/minecraft/entity/effect/StatusEffect;" + "onEntityDamage(Lnet/minecraft/server/world" +
			"/ServerWorld;Lnet/minecraft/entity" + "/LivingEntity;" + "ILnet/minecraft/entity/damage" +
			"/DamageSource;F)V", shift = At.Shift.AFTER))
	public void mixinOnEntityDamage(ServerWorld world, LivingEntity entity, DamageSource source, float amount,
									CallbackInfo ci)
	{
		type.value().updateOnEntityDamage(entity, source, amount, (StatusEffectInstance) (Object) this);
	}

	@Override
	@Unique
	public float interestingWorld$getDamageValueModifierOnDamage(ServerWorld world, DamageSource source, float amount)
	{
		return type.value().getDamageValueModifierOnDamage(world, source, amount, amplifier);
	}

	@Override
	@Unique
	public void interestingWorld$setDuration(int tick)
	{
		duration = tick;
	}
}
