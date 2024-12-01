package org.yang.interestingworld.playerdatamanager;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.network.IWNetwork;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;
import org.yang.interestingworld.rune.IWRuneAbilitys;
import org.yang.interestingworld.util.RuneAbility;

import static org.yang.interestingworld.util.RuneAbility.getAbility;


public class ServerPlayerDataManager
{
	private IWAbstractRuneAbility WeaponAbility = IWRuneAbilitys.DEFAULT_ABILITY;
	private float current_energy = 0;
	private int last_energy = -1;
	private int energyRegenTimer = 0;
	public boolean sweeping = false;
	public boolean charged = false;
	public int chargeRate = -1;
	public boolean isCharging = false;
	public boolean shouldSync = false;

	public void readNbt(NbtCompound nbt)
	{
		current_energy = nbt.getFloat("current_energy");
		energyRegenTimer = nbt.getInt("energyregentimer");
	}

	public void writeNbt(NbtCompound nbt)
	{
		nbt.putFloat("current_energy", current_energy);
		nbt.putInt("energyregentimer", energyRegenTimer);
	}

	public void updateEnergy(ServerPlayerEntity player, ItemStack stack)
	{
		if (energyRegenTimer < Integer.MAX_VALUE) energyRegenTimer++;
		if (player.getHealth() >= player.getMaxHealth())
		{
			if (current_energy < 20)
			{
				if (energyRegenTimer >= 5)
				{
					var hunger = player.getHungerManager();
					int food = hunger.getFoodLevel();
					if (food < 18) return;
					float saturation = hunger.getSaturationLevel();
					if (energyRegenTimer < 45 - (int) saturation * 2) return;
					if (saturation >= 1.0f) hunger.setSaturationLevel(saturation - 1.0f);
					else hunger.setFoodLevel(food - 1);
					current_energy++;
					energyRegenTimer = 0;
				}
			}
			else if (!stack.isEmpty())
			{
				float max = IWUtil.Components.maxEnergy(stack);
				float cur = IWUtil.Components.currentEnergy(stack);
				float rate = stack.getOrDefault(IWComponents.ENERGY_REGEN_RATE, 0.0f);
				if (max > cur && rate != 0.0f)
				{
					var hunger = player.getHungerManager();
					int food = hunger.getFoodLevel();
					if (food < 18) return;
					float saturation = hunger.getSaturationLevel();
					if (energyRegenTimer * rate < 45 - (int) saturation * 2) return;
					stack.set(IWComponents.CURRENT_ENERGY, Math.min(max, cur + 1));
					energyRegenTimer = 0;
				}
			}
		}
	}


	public void tick(ServerPlayerEntity player)
	{
		ItemStack stack = player.getWeaponStack();
		updateEnergy(player, stack);
		if (!stack.isEmpty() && stack.getItem() instanceof EnergyToolItem)
		{
			var ability = getAbility(stack);
			if (!ability.canWork())
			{
				if(WeaponAbility.index != 0)
				{
					WeaponAbility.onLeave(player, this);
					WeaponAbility = IWRuneAbilitys.DEFAULT_ABILITY;
					shouldSync = true;
				}
			}
			else if (ability != WeaponAbility)
			{
				WeaponAbility.onLeave(player, this);
				ability.onEnter(player, this);
				WeaponAbility = ability;
				shouldSync = true;
			}
		}
		else if (WeaponAbility.index != 0)
		{
			WeaponAbility.onLeave(player, this);
			WeaponAbility = IWRuneAbilitys.DEFAULT_ABILITY;
			shouldSync = true;
		}
		WeaponAbility.serverPlayerWeaponTick(player, this, stack);
		int ce = (int) current_energy;
		if (last_energy != ce)
		{
			last_energy = ce;
			ServerPlayNetworking.send(player, new IWNetwork.PlayerEnergyPayload(last_energy));
		}
		if (shouldSync)
		{
			shouldSync = false;
			ServerPlayNetworking.send(player, new IWNetwork.AbilityPayload(WeaponAbility, this, null));
		}
	}

	public boolean extractAutomicEnergy(ItemStack stack, ServerPlayerEntity user, float amount)
	{
		var ab = RuneAbility.getAbility(stack);
		if (ab.canWork())
		{
			var fh = IWUtil.Return.FloatHolder.getInstance(amount);
			var res = ab.extractPower(fh);
			if (res == IWUtil.Return.RETURNTRUE)
			{
				return true;
			}
		}
		float e = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0.0f);
		if (e > 0.0f)
		{
			if (current_energy + e >= amount)
			{
				if (e < amount)
				{
					current_energy -= amount - e;
					stack.set(IWComponents.CURRENT_ENERGY, 0.0f);
				}
				else stack.set(IWComponents.CURRENT_ENERGY, e - amount);
				return true;
			}
		}
		else if (current_energy >= amount)
		{
			current_energy -= amount;
			return true;
		}
		return false;
	}

	public boolean tryExtractAutomicEnergy(ItemStack stack, ServerPlayerEntity user, float amount)
	{
		var ab = RuneAbility.getAbility(stack);
		if (ab.canWork())
		{
			var fh = IWUtil.Return.FloatHolder.getInstance(amount);
			var res = ab.extractPower(fh);
			if (res == IWUtil.Return.RETURNTRUE)
			{
				return true;
			}
		}
		float e = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0.0f);
		return current_energy + e >= amount;
	}

	public float getCurrentEnergy()
	{
		return current_energy;
	}
}
