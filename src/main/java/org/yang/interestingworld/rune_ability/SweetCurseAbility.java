package org.yang.interestingworld.rune_ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.yang.interestingworld.effect.IWEffects;
import org.yang.interestingworld.entity.player.IWClientPlayerData;
import org.yang.interestingworld.entity.player.IWServerPlayerData;
import org.yang.interestingworld.particle_type.IWParticleTypes;
import org.yang.interestingworld.util.IWLivingEntityUtil;
import org.yang.interestingworld.util.IWParticleUtil;
import org.yang.interestingworld.util.IWSoundUtil;
import org.yang.interestingworld.util.IWStatusEffectUtil;
import org.yang.interestingworld.util.style.Color;

import static org.yang.interestingworld.util.Return.FAIL;
import static org.yang.interestingworld.util.Return.PASS;

public class SweetCurseAbility extends RuneAbility
{
	public static final short AbilityDuration = 60;
	public static final int RegenAmplifier = 1;
	public static final int EffectDuration = 160;
	public static final int HurtingAmplifier = 4;
	public static final double AttackMaxLength = 3;
	public static final float EnergyConsume = 10;

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
			var data = player.getIWServerPlayerData();
			if (!data.isAbilityOn() || data.chargeRate < AbilityDuration ||
				!data.extractAutomicEnergy(stack, EnergyConsume)) return FAIL;
			double x = user.getX();
			double y = user.getY();
			double z = user.getZ();
			IWParticleUtil.spawnParticleAtPos(world, IWParticleTypes.SWEETCURSE_CYCLE, x, y + 0.001, z);
			IWParticleUtil.spawnParticleAtPos(world, ParticleTypes.HEART, x, y + 0.5, z, 16, 2, 1.5, 2, 0);
			data.chargeRate = 0;
			data.shouldSync = true;
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
			return FAIL;
		}
		return PASS;
	}

	@Override
	public void serverPlayerWeaponTick(PlayerEntity entity, IWServerPlayerData data, ItemStack stack)
	{
		if (data.chargeRate < AbilityDuration)
		{
			data.chargeRate++;
			data.shouldSync = true;
		}
	}

	@Override
	public UseAction getUseAction(ItemStack stack, UseAction before)
	{
		return UseAction.BOW;
	}

	@Override
	public void onEnter(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.chargeRate = 0;
	}

	@Override
	public void onLeave(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.chargeRate = -1;
	}

	@Override
	public int abilityBarForegroundColor(IWClientPlayerData data)
	{
		return data.client_ability_on ? Color.PINK_RGB : Color.GRAY_RGB;
	}

	@Override
	public int abilityProcess(IWClientPlayerData data)
	{
		return data.charge_rate16;
	}

	@Override
	public void writeClientRenderDataToBuf(IWServerPlayerData data, RegistryByteBuf buf)
	{
		buf.writeInt(data.chargeRate);
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, ByteBuf buf)
	{
		int chargeRate = buf.readInt();
		int c = Math.clamp(chargeRate * 16L / AbilityDuration, 0, 16);
		if (data.client_ability_on && c == 16 && data.charge_rate16 != 16)
		{
			playChargedOverSound(entity);
		}
		data.charge_rate16 = c;
	}
}
