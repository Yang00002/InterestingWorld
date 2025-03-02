package org.yang.iw.ability;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.effect.IWEffects;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.particle_type.IWParticleTypes;
import org.yang.iw.util.IWLivingEntityUtil;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.IWSoundUtil;
import org.yang.iw.util.IWStatusEffectUtil;
import org.yang.iw.util.style.Color;

import static org.yang.iw.util.Return.FAIL;
import static org.yang.iw.util.Return.PASS;

public class SweetCurseAbility extends CommonAbility
{
	public static final short AbilityDuration = 60;
	public static final int RegenAmplifier = 1;
	public static final int EffectDuration = 160;
	public static final int HurtingAmplifier = 4;
	public static final double AttackMaxLength = 3;
	public static final float EnergyConsume = 10;

	SweetCurseAbility(IntrusiveTag<AbilityHeart> intrusiveTag)
	{
		super(intrusiveTag);
	}

	public int getColor()
	{
		return Color.PINK_RGB;
	}

	public String id()
	{
		return "sweetcurse";
	}

	@Override
	public byte use(World world, PlayerEntity user, Hand hand, ItemStack stack)
	{
		if (user instanceof ServerPlayerEntity player)
		{
			var data = player.interestingWorld$getIWServerPlayerData();
			if (!data.isAbilityOn() || data.isInCooldown(this) || !data.extractAtomicEnergy(stack, EnergyConsume))
				return FAIL;
			double x = user.getX();
			double y = user.getY();
			double z = user.getZ();
			IWParticleUtil.spawnParticleAtPos(world, IWParticleTypes.SWEETCURSE_CYCLE, x, y + 0.001, z);
			IWParticleUtil.spawnParticleAtPos(world, ParticleTypes.HEART, x, y + 0.5, z, 16, 2, 1.5, 2, 0);
			StatusEffectInstance instance = new StatusEffectInstance(StatusEffects.REGENERATION, EffectDuration);
			IWSoundUtil.playSoundToPlayer(player, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS);
			IWLivingEntityUtil.influenceEntityAround(user, AttackMaxLength, (attacker, entity, distance) -> {
				if (entity.canHaveStatusEffect(instance))
				{
					IWStatusEffectUtil.addStatusEffect(entity, StatusEffects.REGENERATION, EffectDuration,
							RegenAmplifier);
					IWStatusEffectUtil.addHiddenStatusEffect(entity, IWEffects.HURTING, EffectDuration,
							HurtingAmplifier);
				}
			});
			data.setCooldown(this, AbilityDuration);
			return FAIL;
		}
		return PASS;
	}

	@Override
	public AbilityCooldownGroup getCooldownGroup()
	{
		return AbilityCooldownGroup.ASSISTANCE;
	}

	@Override
	public UseAction getUseAction(ItemStack stack, UseAction before)
	{
		return UseAction.BOW;
	}

	@Override
	public AbstractAbility getToolRenderAbility()
	{
		return IWAbilities.SWEETCURSE_ABILITY;
	}
}
