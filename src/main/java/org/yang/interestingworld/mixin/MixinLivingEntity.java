package org.yang.interestingworld.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.yang.interestingworld.IWEffects;
import org.yang.interestingworld.IWEnchantments;
import org.yang.interestingworld.IWUtil;


@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements Attackable
{
	//@Shadow
	//protected float lastDamageTaken;
	//@Shadow
	//public int maxHurtTime;
	//@Unique
	//private int maxHurtTimeCache = 0;
	//@Unique
	//private int hurtTimeCache = 0;
	//@Unique
	//private int timeUntilRegenCache = 0;
	//@Unique
	//private float lastDamageTakenCache = 0;


	@Shadow
	protected float lastDamageTaken;

	public MixinLivingEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	/*
	@Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "INVOKE", target =
	"Lnet" +
																												 "/minecraft/entity/LimbAnimator;setSpeed(F)V"))
	private void injected(CallbackInfoReturnable<Boolean> cir)
	{
		maxHurtTimeCache = maxHurtTime;
		hurtTimeCache = ((LivingEntity) (Object) this).hurtTime;
		timeUntilRegenCache = timeUntilRegen;
		lastDamageTakenCache = lastDamageTaken;
	}

	@Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "INVOKE", target =
	"Lnet" +
																												 "/minecraft/entity/damage/DamageSource;getAttacker()Lnet/minecraft/entity/Entity;"))
	private void injected2(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if (source.isIn(DamageTypeTags.BYPASSES_COOLDOWN))
		{
			maxHurtTime = maxHurtTimeCache;
			((LivingEntity) (Object) this).hurtTime = hurtTimeCache;
			timeUntilRegen = timeUntilRegenCache;
			lastDamageTaken = lastDamageTakenCache;
		}
	}*/
	@Unique
	private int hurtGate = 20;

	@Redirect(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "FIELD", target =
			"Lnet/minecraft/entity/LivingEntity;lastDamageTaken:F", opcode = Opcodes.PUTFIELD, ordinal = 1))
	private void hurtAmount(LivingEntity instance, float value, @Local(argsOnly = true) DamageSource source)
	{
		if (!source.isIn(DamageTypeTags.BYPASSES_COOLDOWN))
		{
			lastDamageTaken = value;
		}
	}

	@ModifyConstant(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", constant = @Constant(intValue =
			20))
	private int hurtCooldown(int cooldown, @Local(argsOnly = true) DamageSource source)
	{
		if (source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) return timeUntilRegen;
		LivingEntity entity = (LivingEntity) (Object) this;
		StatusEffectInstance ins = entity.getStatusEffect(IWEffects.COOLDOWN);
		if (ins == null) return 20;
		return Math.max(19 - ins.getAmplifier(), hurtGate + 1);
	}

	@ModifyConstant(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", constant = @Constant(floatValue =
			10.0f))
	private float hurtGate(float cooldownGate, @Local(argsOnly = true) DamageSource source)
	{
		if (source.isIn(DamageTypeTags.BYPASSES_COOLDOWN))
		{
			hurtGate = (int) cooldownGate;
			return cooldownGate;
		}
		ItemStack weapon = source.getWeaponStack();
		if (weapon == null || weapon.isEmpty())
		{
			hurtGate = (int) cooldownGate;
			return cooldownGate;
		}
		int level = IWUtil.EnergyTool.getEnchantmentLevel(this.getWorld(), weapon, IWEnchantments.FAST_HIT);
		hurtGate = Math.min(10 + level, 19);
		return hurtGate;
	}

}
