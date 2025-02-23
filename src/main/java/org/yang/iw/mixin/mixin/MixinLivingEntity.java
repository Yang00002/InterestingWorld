package org.yang.iw.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.yang.iw.IWEntityAttributes;
import org.yang.iw.effect.IWEffects;
import org.yang.iw.mixin.mixin_interface.InterfaceLivingEntity;

import java.util.Collection;
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

	@Shadow
	public abstract Collection<StatusEffectInstance> getStatusEffects();

	public MixinLivingEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Unique
	private int invulnerableTicks = 10;

	@Redirect(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;" +
																	"lastDamageTaken:F", opcode = Opcodes.PUTFIELD,
			ordinal = 1))
	private void mixinDamage(LivingEntity instance, float value, @Local(argsOnly = true) DamageSource source)
	{
		if (!source.isIn(DamageTypeTags.BYPASSES_COOLDOWN))
		{
			lastDamageTaken = value;
		}
	}


	// timeUR 在受伤时被设置为 20. 其每刻减小，减到 <= 10 则可受伤
	@ModifyConstant(method = "damage", constant = @Constant(floatValue = 10.0f, ordinal = 0))
	private float neglectInvulnerableTicks(float cooldownGate, @Local(argsOnly = true) DamageSource source)
	{
		if (source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) return cooldownGate;
		Entity attacker = source.getAttacker();
		if (!(attacker instanceof LivingEntity entity) ||
			!entity.getAttributes().hasAttribute(IWEntityAttributes.ATTACK_DURATION_NEGLECT)) return cooldownGate;
		return 10 + Math.min(invulnerableTicks - 1,
				(int) entity.getAttributeValue(IWEntityAttributes.ATTACK_DURATION_NEGLECT));
	}

	@ModifyConstant(method = "damage", constant = @Constant(intValue = 20))
	private int setInvulnerableTicks(int cooldown, @Local(argsOnly = true) DamageSource source)
	{
		if (source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) return timeUntilRegen;
		LivingEntity entity = (LivingEntity) (Object) this;
		StatusEffectInstance ins = entity.getStatusEffect(IWEffects.COOLDOWN);
		invulnerableTicks = ins == null ? 10 : Math.max(1, 10 - ins.getAmplifier());
		return 10 + invulnerableTicks;
	}

	@Inject(method = "onStatusEffectsRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect" +
																				   "/StatusEffectInstance;" +
																				   "getEffectType" +
																				   "()Lnet/minecraft/registry/entry" +
																				   "/RegistryEntry;"), locals =
			LocalCapture.CAPTURE_FAILSOFT)
	private void MixinOnStatusEffectRemoved(Collection<StatusEffectInstance> effects, CallbackInfo ci, @Local(ordinal
			= 0) StatusEffectInstance effect)
	{
		effect.getEffectType().value().onRemoveEffect((LivingEntity) (Object) this, effect.getAmplifier());
	}

	@ModifyVariable(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;" +
																		  "despawnCounter:I"), argsOnly = true)
	public float modifyDamageValue(float amount, @Local(argsOnly = true, ordinal = 0) ServerWorld world,
								   @Local(argsOnly = true, ordinal = 0) DamageSource source)
	{
		float sum = amount;
		for (var effectInstance : getStatusEffects())
			sum += effectInstance.interestingWorld$getDamageValueModifierOnDamage(world, source, amount);
		var at = getAttributes();
		if (at.hasAttribute(IWEntityAttributes.HURT_DAMAGE_MULTIPLIER))
			sum *= (float) at.getValue(IWEntityAttributes.HURT_DAMAGE_MULTIPLIER);
		return sum;
	}


	@Override
	@Unique
	public boolean interestingWorld$removeStatusEffectVanilla(RegistryEntry<StatusEffect> effect)
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

			for (Entity entity : this.getPassengerList())
			{
				if (entity instanceof ServerPlayerEntity serverPlayerEntity)
				{
					serverPlayerEntity.networkHandler.sendPacket(
							new RemoveEntityStatusEffectS2CPacket(this.getId(), effect.getEffectType()));
				}
			}
			updateAttributes();
		}
	}

	@Inject(method = "createLivingAttributes", at = @At(value = "RETURN"), cancellable = true)
	private static void addAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir)
	{
		cir.setReturnValue(cir.getReturnValue().add(IWEntityAttributes.ATTACK_DURATION_NEGLECT)
				.add(IWEntityAttributes.HURT_DAMAGE_MULTIPLIER));
	}
}
