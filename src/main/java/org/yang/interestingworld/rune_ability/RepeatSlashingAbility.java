package org.yang.interestingworld.rune_ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.interestingworld.IWDamageTypes;
import org.yang.interestingworld.IWSounds;
import org.yang.interestingworld.entity.player.IWClientPlayerData;
import org.yang.interestingworld.entity.player.IWServerPlayerData;
import org.yang.interestingworld.util.IWLivingEntityUtil;
import org.yang.interestingworld.util.IWParticleUtil;
import org.yang.interestingworld.util.IWSoundUtil;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import java.util.List;

public class RepeatSlashingAbility extends RuneAbility
{
	public static final int AbilityCooldown = 40;
	public static final int EnergyConsume = 20;
	public static final int SecondAttackTimeLimit = 80;
	public static final int ThirdAttackTimeLimit = 80;
	public static final int FirstAttackReturnEnergy = 15;
	public static final int SecondAttackReturnEnergy = 10;
	public static final int FirstAttackDamage = 5;
	public static final int SecondAttackDamage = 5;
	public static final int ThirdAttackDamage = 8;
	public static final float FirstAttackAngleCosine = 0.5f;
	public static final float SecondAttackAngleCosine = 0.4f;
	public static final float ThirdAttackAngleCosine = 0.3f;
	public static final float ThirdAttackKnockback = 0.4f;
	public static final int FirstAttackRange = 3;
	public static final int SecondAttackRange = 3;
	public static final int ThirdAttackRange = 5;

	@Override
	public int level()
	{
		return 3;
	}

	public int getColor()
	{
		return Color.RED_RGB;
	}
	public String id()
	{
		return "repeatslashing";
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return EnergyToolDataFlag.getFromItemStack(stack).canSweep();
	}

	@Override
	public void serverPlayerWeaponTick(PlayerEntity entity, IWServerPlayerData data, ItemStack stack)
	{
		switch (data.chargeStep)
		{
			case 3 ->
			{
				if (data.chargeRate < AbilityCooldown)
				{
					data.chargeRate++;
					data.shouldSync = true;
				}
			}
			case 2 ->
			{
				if (data.chargeRate > 0) data.chargeRate--;
				else
				{
					data.returnEnergyToTool(stack, FirstAttackReturnEnergy);
					data.chargeStep = 3;
				}
				data.shouldSync = true;
			}
			case 1 ->
			{
				if (data.chargeRate > 0) data.chargeRate--;
				else
				{
					data.returnEnergyToTool(stack, SecondAttackReturnEnergy);
					data.chargeStep = 3;
				}
				data.shouldSync = true;
			}
		}
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		if (attacker instanceof ServerPlayerEntity player)
		{
			var manager = player.getIWServerPlayerData();
			if (manager.isAbilityOn() && manager.sweeping && (manager.chargeStep < 3 ||
															  (manager.chargeRate >= AbilityCooldown &&
															   manager.extractAutomicEnergy(stack, EnergyConsume))))
			{
				int damage;
				int range;
				float angle;
				int step = manager.chargeStep;
				manager.chargeStep--;
				switch (step)
				{
					case 3 ->
					{
						damage = FirstAttackDamage;
						range = FirstAttackRange;
						angle = FirstAttackAngleCosine;
						manager.chargeRate = SecondAttackTimeLimit;
					}
					case 2 ->
					{
						damage = SecondAttackDamage;
						range = SecondAttackRange;
						angle = SecondAttackAngleCosine;
						manager.chargeRate = ThirdAttackTimeLimit;
					}
					case 1 ->
					{
						damage = ThirdAttackDamage;
						range = ThirdAttackRange;
						angle = ThirdAttackAngleCosine;
						manager.chargeStep = 3;
						manager.chargeRate = 0;
					}
					default ->
					{
						damage = 0;
						range = 0;
						angle = 0.0f;
					}
				}
				manager.shouldSync = true;
				World world = attacker.getWorld();
				var knox = MathHelper.sin(attacker.getYaw() * 0.017453292F);
				var knoz = -MathHelper.cos(attacker.getYaw() * 0.017453292F);
				var damageSource = new DamageSource(IWDamageTypes.ENERGEE_MELEE_entry.get(), attacker);
				var pc = 3 + ((3 - step) >> 1);
				IWSoundUtil.playSoundToPlayer(player, IWSounds.DOUBLE_SWEEP, SoundCategory.PLAYERS);
				IWLivingEntityUtil.sweepEntity(attacker, angle, range, target, (attacker1, entity, distance) -> {
					if (step > 2) entity.takeKnockback(ThirdAttackKnockback, knox, knoz);
					entity.damage(damageSource, damage);
					double x = entity.getX();
					double y = entity.getBodyY(0.5);
					double z = entity.getZ();
					IWParticleUtil.spawnParticlesAtPos(world, ParticleTypes.SWEEP_ATTACK, x, y, z, pc, 0.5, 0.5, 0.5);
				});
			}
		}
	}

	@Override
	public void onEnter(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.chargeRate = 0;
		manager.chargeStep = 3;
	}

	@Override
	public void onLeave(PlayerEntity entity, IWServerPlayerData manager)
	{
		manager.chargeRate = -1;
		manager.chargeStep = -1;
	}

	@Override
	public int abilityBarForegroundColor(IWClientPlayerData data)
	{
		return data.server_ability_on_opt ? Color.RED_RGB : Color.GRAY_RGB;
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
		buf.writeInt(data.chargeStep);
		buf.writeBoolean(data.isAbilityOn());
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, ByteBuf buf)
	{
		int chargeRate = buf.readInt();
		int chargeStep = buf.readInt();
		data.server_ability_on_opt = buf.readBoolean();
		int c;
		switch (chargeStep)
		{
			case 3 -> c = Math.clamp(chargeRate * 16L / AbilityCooldown, 0, 16);
			case 2 -> c = Math.clamp(chargeRate * 16L / SecondAttackTimeLimit, 0, 16);
			case 1 -> c = Math.clamp(chargeRate * 16L / ThirdAttackTimeLimit, 0, 16);
			default -> c = 0;
		}
		if (data.server_ability_on_opt && chargeStep == 3 && c == 16 && data.charge_rate16 != 16)
		{
			playChargedOverSound(entity);
		}
		data.charge_rate16 = c;
		data.shown_number = chargeStep;
	}

	@Override
	public boolean shouldRenderAbilityBar(IWClientPlayerData data)
	{
		return data.server_ability_on_opt || data.charge_rate16 < 16;
	}

	@Override
	public boolean shouldRenderAbilityNumber(IWClientPlayerData data)
	{
		return data.server_ability_on_opt && (data.shown_number < 3 || data.charge_rate16 == 16);
	}

	@Override
	public int abilityNumber(IWClientPlayerData data)
	{
		return data.shown_number;
	}

	@Override
	public void onServerAbilityClose(ServerPlayerEntity player, IWServerPlayerData data)
	{
		ItemStack weaponStack = player.getWeaponStack();
		if (!weaponStack.isEmpty())
		{
			switch (data.chargeStep)
			{
				case 2 ->
				{
					data.chargeRate = 0;
					data.chargeStep = 3;
					data.shouldSync = true;
					data.returnEnergyToTool(weaponStack, FirstAttackReturnEnergy);
				}
				case 1 ->
				{
					data.chargeRate = 0;
					data.chargeStep = 3;
					data.shouldSync = true;
					data.returnEnergyToTool(weaponStack, SecondAttackReturnEnergy);
				}
			}
		}
		data.shouldSync = true;
	}

	public void onServerAbilityOpen(ServerPlayerEntity player, IWServerPlayerData data)
	{
		data.shouldSync = true;
	}

	@Override
	public int abilityNumberColor()
	{
		return Color.RED_RGB;
	}

	@Override
	public AbstractRuneAbility getToolIndexParent()
	{
		return IWRuneAbilities.SLASHING_ABILITY;
	}

	@Override
	public AbstractRuneAbility getRuneIndexParent()
	{
		return IWRuneAbilities.SLASHING_ABILITY;
	}

}
