package org.yang.interestingworld.entity.player;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.network.payload.S2CAbilityDataPayload;
import org.yang.interestingworld.network.payload.S2CPlayerEnergyDataPayload;
import org.yang.interestingworld.rune_ability.AbstractRuneAbility;
import org.yang.interestingworld.rune_ability.IWRuneAbilities;
import org.yang.interestingworld.util.IWRuneAbilityUtil;

import static org.yang.interestingworld.util.IWRuneAbilityUtil.getAbility;
import static org.yang.interestingworld.util.Return.RETURNTRUE;


public class IWServerPlayerData
{
	private AbstractRuneAbility WeaponAbility = IWRuneAbilities.DEFAULT_ABILITY;
	private float current_energy = 0;
	private int last_energy = -1;
	private int energyRegenTimer = 0;
	private boolean ability_on = true;

	public boolean sweeping = false;
	public boolean charged = false;
	public int chargeStep = -1;
	public int chargeRate = -1;
	public boolean isCharging = false;
	public boolean shouldSync = false;
	public int forging_seed = 0;

	public boolean isAbilityOn()
	{
		return ability_on;
	}

	public void readNbt(NbtCompound nbt)
	{
		current_energy = nbt.getFloat("current_energy");
		energyRegenTimer = nbt.getInt("energyregentimer");
		forging_seed = nbt.getInt("forging_seed");
		ability_on = nbt.getBoolean("ability_on");
	}

	public void writeNbt(NbtCompound nbt)
	{
		nbt.putFloat("current_energy", current_energy);
		nbt.putInt("energyregentimer", energyRegenTimer);
		nbt.putInt("forging_seed", forging_seed);
		nbt.putBoolean("ability_on", ability_on);
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
		sweeping = false;
		if (!stack.isEmpty() && stack.getItem() instanceof EnergyToolItem)
		{
			var ability = getAbility(stack);
			if (!ability.canWork())
			{
				if (WeaponAbility.index != 0)
				{
					WeaponAbility.onLeave(player, this);
					WeaponAbility = IWRuneAbilities.DEFAULT_ABILITY;
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
			WeaponAbility = IWRuneAbilities.DEFAULT_ABILITY;
			shouldSync = true;
		}
		WeaponAbility.serverPlayerWeaponTick(player, this, stack);
		int ce = (int) current_energy;
		if (last_energy != ce)
		{
			last_energy = ce;
			ServerPlayNetworking.send(player, new S2CPlayerEnergyDataPayload(last_energy));
		}
		if (shouldSync)
		{
			shouldSync = false;
			ServerPlayNetworking.send(player, new S2CAbilityDataPayload(WeaponAbility, this, null));
		}
	}

	public boolean extractAutomicEnergy(ItemStack stack, float amount)
	{
		var ab = IWRuneAbilityUtil.getAbility(stack);
		if (ab.canWork())
		{
			var fh = new MutableFloat(amount);
			var res = ab.extractPower(fh);
			if (res == RETURNTRUE)
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

	public boolean tryExtractAutomicEnergy(ItemStack stack, float amount)
	{
		var ab = IWRuneAbilityUtil.getAbility(stack);
		if (ab.canWork())
		{
			var fh = new MutableFloat(amount);
			var res = ab.extractPower(fh);
			if (res == RETURNTRUE)
			{
				return true;
			}
		}
		float e = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0.0f);
		return current_energy + e >= amount;
	}

	public void returnEnergyToTool(ItemStack stack, float amount)
	{
		float max = stack.getOrDefault(IWComponents.MAX_ENERGY, 0f);
		float cur = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0f);
		stack.set(IWComponents.CURRENT_ENERGY, Math.min(cur + amount, max));
	}

	public void returnEnergyToPlayerAndTool(ItemStack stack, float amount)
	{
		if (current_energy + amount >= 20f)
		{
			amount += current_energy - 20f;
			current_energy = 20f;
			if (amount > 0f)
			{
				float max = stack.getOrDefault(IWComponents.MAX_ENERGY, 0f);
				float cur = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0f);
				stack.set(IWComponents.CURRENT_ENERGY, Math.min(cur + amount, max));
			}
		}
		else current_energy += amount;
	}

	public void returnEnergyToPlayer(float amount)
	{
		if (current_energy + amount >= 20f) current_energy = 20f;
		else current_energy += amount;
	}

	public float getCurrentEnergy()
	{
		return current_energy;
	}

	public void writeS2CInitializeDataToBuffer(RegistryByteBuf buf)
	{
		buf.writeBoolean(ability_on);
	}

	public void changeAbilityOpenCondition(boolean next, ServerPlayerEntity player)
	{
		if (ability_on != next)
		{
			if (next) WeaponAbility.onServerAbilityOpen(player, this);
			else WeaponAbility.onServerAbilityClose(player, this);
		}
		ability_on = next;
	}
}
