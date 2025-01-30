package org.yang.iw.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.effect.IWEffects;
import org.yang.iw.enchant.IWEnchantments;
import org.yang.iw.mixin.mixin_interface.InterfaceLivingEntity;

import java.util.Map;


@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements Attackable, InterfaceLivingEntity
{
	@Shadow
	protected float lastDamageTaken;

	@Shadow
	public abstract Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects();

	@Shadow
	public abstract @Nullable StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

	@Shadow
	public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

	@Shadow
	public abstract @Nullable StatusEffectInstance removeStatusEffectInternal(RegistryEntry<StatusEffect> effect);

	@Shadow
	private boolean effectsChanged;

	@Shadow
	protected abstract void updateAttributes();

	@Shadow
	public abstract AttributeContainer getAttributes();

	public MixinLivingEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Unique
	private int hurtGate = 20;

	@Redirect(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "FIELD", target =
			"Lnet/minecraft/entity/LivingEntity;lastDamageTaken:F", opcode = Opcodes.PUTFIELD, ordinal = 1))
	private void mixinDamage(LivingEntity instance, float value, @Local(argsOnly = true) DamageSource source)
	{
		if (!source.isIn(DamageTypeTags.BYPASSES_COOLDOWN))
		{
			lastDamageTaken = value;
		}
	}

	@ModifyVariable(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), argsOnly = true)
	private float mixinDamage2(float y)
	{
		var st = getStatusEffect(IWEffects.HURTING);
		if (st != null)
		{
			var am = st.getAmplifier() + 1;
			if (am != 1) return y * (1 + am / 10.0f);
		}
		return y;
	}

	@ModifyConstant(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", constant = @Constant(intValue =
			20))
	private int mixinDamage3(int cooldown, @Local(argsOnly = true) DamageSource source)
	{
		if (source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) return timeUntilRegen;
		LivingEntity entity = (LivingEntity) (Object) this;
		StatusEffectInstance ins = entity.getStatusEffect(IWEffects.COOLDOWN);
		if (ins == null) return 20;
		return Math.max(19 - ins.getAmplifier(), hurtGate + 1);
	}

	@ModifyConstant(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", constant = @Constant(floatValue =
			10.0f, ordinal = 0))
	private float mixinDamage4(float cooldownGate, @Local(argsOnly = true) DamageSource source)
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
		int level = weapon.getEnchantments().getLevel(IWEnchantments.ENTRY_FAST_HIT.get());
		hurtGate = Math.min(10 + level, 19);
		return hurtGate;
	}

	@Inject(method = "onStatusEffectRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect" +
																				  "/StatusEffectInstance;" +
																				  "getEffectType" +
																				  "()Lnet/minecraft/registry/entry" +
																				  "/RegistryEntry;"))
	private void MixinOnStatusEffectRemoved(StatusEffectInstance effect, CallbackInfo ci)
	{
		effect.getEffectType().value().onRemoveEffect((LivingEntity) (Object) this, effect.getAmplifier());
	}

	@Override
	@Unique
	public boolean removeStatusEffectVanilla(RegistryEntry<StatusEffect> effect)
	{
		StatusEffectInstance statusEffectInstance = removeStatusEffectInternal(effect);
		if (statusEffectInstance != null)
		{
			vanillaOnStatusEffectRemoved(statusEffectInstance);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Unique
	private void vanillaOnStatusEffectRemoved(StatusEffectInstance effect)
	{
		effectsChanged = true;
		if (!this.getWorld().isClient)
		{
			effect.getEffectType().value().onRemoved(getAttributes());
			updateAttributes();

			for (Entity entity : this.getPassengerList())
			{
				if (entity instanceof ServerPlayerEntity serverPlayerEntity)
				{
					serverPlayerEntity.networkHandler.sendPacket(
							new RemoveEntityStatusEffectS2CPacket(this.getId(), effect.getEffectType()));
				}
			}
		}
	}
}
