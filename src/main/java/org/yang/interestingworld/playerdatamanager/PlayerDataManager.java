package org.yang.interestingworld.playerdatamanager;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.network.IWNetwork;

public class PlayerDataManager
{
	private int shown_energy = 0;
	private float current_energy = 0;
	private float last_energy = -1;
	private int energyRegenTimer = 0;
	private int ticker = 0;
	public boolean sweeping = false;
	public boolean charged = false;
	public int timing = -1;
	public int maxtiming = -1;

	public void readNbt(NbtCompound nbt)
	{
		current_energy = nbt.getFloat("current_energy");
		energyRegenTimer = nbt.getInt("energyregentimer");
		ticker = nbt.getInt("energyticker");
		sweeping = nbt.getBoolean("is_sweeping");
	}

	public void writeNbt(NbtCompound nbt)
	{
		nbt.putFloat("current_energy", current_energy);
		nbt.putInt("energyregentimer", energyRegenTimer);
		nbt.putInt("energyticker", ticker);
		nbt.putBoolean("is_sweeping", sweeping);
	}

	public void update(PlayerEntity player)
	{
		if (ticker < 25200) ticker++;
		else ticker = 0;
		if (energyRegenTimer < 45) energyRegenTimer++;
		if (current_energy < 40 && energyRegenTimer >= 5 && player.getHealth() >= player.getMaxHealth())
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

	public boolean extractAutomicEnergy(ItemStack stack, PlayerEntity user, float amount)
	{
		var ab = IWUtil.RuneAbility.getAbility(stack);
		if (ab.canWork())
		{
			var fh = IWUtil.Return.FloatHolder.getInstance(amount);
			var res = ab.extractPower(fh);
			if (res == IWUtil.Return.RETURNTRUE)
			{
				return true;
			}
		}
		if (stack.hasEnchantments())
		{
			var level = IWUtil.EnergyTool.getEnchantmentLevel(user.getWorld(), stack, Enchantments.UNBREAKING);
			if (level >= 10) return true;
			amount *= 1.0f - 0.1f * level;
		}
		if (current_energy >= amount)
		{
			current_energy -= amount;
			return true;
		}
		return false;
	}

	public boolean tryExtractAutomicEnergy(ItemStack stack, PlayerEntity user, float amount)
	{
		var ab = IWUtil.RuneAbility.getAbility(stack);
		if (ab.canWork())
		{
			var fh = IWUtil.Return.FloatHolder.getInstance(amount);
			var res = ab.extractPower(fh);
			if (res == IWUtil.Return.RETURNTRUE)
			{
				return true;
			}
		}
		if (stack.hasEnchantments())
		{
			var level = IWUtil.EnergyTool.getEnchantmentLevel(user.getWorld(), stack, Enchantments.UNBREAKING);
			if (level >= 10) return true;
			amount *= 1.0f - 0.1f * level;
		}
		return current_energy >= amount;
	}

	public void syncWithClient(ServerPlayerEntity player)
	{
		if (last_energy != current_energy)
		{
			shown_energy = (int) current_energy;
			ServerPlayNetworking.send(player, new IWNetwork.PlayerEnergyPayload(shown_energy));
			last_energy = current_energy;
		}
	}

	public void setShownEnergy(int energy)
	{
		shown_energy = energy;
	}

	public int getShownEnergy()
	{
		return shown_energy;
	}

	public int getTicker()
	{
		return ticker;
	}

}
