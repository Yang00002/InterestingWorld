package org.yang.iw.rune_ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import org.yang.iw.IWDamageTypes;
import org.yang.iw.IWSounds;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.util.IWLivingEntityUtil;
import org.yang.iw.util.IWParticleUtil;
import org.yang.iw.util.IWSoundUtil;
import org.yang.iw.util.style.Color;

public class SlashingAbility extends RuneAbility
{
	public static final short AbilityDuration = 30;
	public static final float AbilityDamage = 3;
	public static final double AttackMaxAngleCosine = 0.5;
	public static final double AttackMaxLength = 3.5;
	public static final float EnergyCosume = 5;
	public static final double KnockbackDistance = 0.5;

	public String id()
	{
		return "slashing";
	}

	public int getColor()
	{
		return Color.RED_RGB;
	}


	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return EnergyToolDataFlag.fromItemStack(stack).canSweep();
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
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (attacker instanceof ServerPlayerEntity player)
		{
			var manager = player.getIWServerPlayerData();
			if (manager.isAbilityOn() && manager.sweeping && manager.chargeRate >= AbilityDuration &&
				manager.extractAutomicEnergy(stack, EnergyCosume))
			{
				manager.chargeRate = 0;
				manager.shouldSync = true;
				ServerWorld world = (ServerWorld) player.getWorld();
				var knox = MathHelper.sin(attacker.getYaw() * 0.017453292F);
				var knoz = -MathHelper.cos(attacker.getYaw() * 0.017453292F);
				var damageSource = new DamageSource(IWDamageTypes.ENERGEE_MELEE_ENTRY.get(), attacker);
				IWSoundUtil.playSoundToPlayer(player, IWSounds.DOUBLE_SWEEP, SoundCategory.PLAYERS);
				IWLivingEntityUtil.sweepEntity(attacker, AttackMaxAngleCosine, AttackMaxLength, target,
						(attacker1, entity, distance) -> {
							entity.takeKnockback(KnockbackDistance, knox, knoz);
							entity.damage(world, damageSource, AbilityDamage);
							double x = entity.getX();
							double y = entity.getBodyY(0.5);
							double z = entity.getZ();
							IWParticleUtil.spawnParticlesAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z, 3, 0.5, 0.5,
									0.5);
						});
			}
		}
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
		return data.client_ability_on ? Color.RED_RGB : Color.GRAY_RGB;
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
