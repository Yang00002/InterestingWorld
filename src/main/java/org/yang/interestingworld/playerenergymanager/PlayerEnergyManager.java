package org.yang.interestingworld.playerenergymanager;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.yang.interestingworld.network.IWNetwork;

public class PlayerEnergyManager
{
	private int shown_energy = 0;
	private float current_energy = 0;
	private float last_energy = -1;
	private int energyRegenTimer = 0;
	private int ticker = 0;

	public void readNbt(NbtCompound nbt)
	{
		current_energy = nbt.getFloat("current_energy");
		energyRegenTimer = nbt.getInt("energyregentimer");
		ticker = nbt.getInt("energyticker");
	}

	public void writeNbt(NbtCompound nbt)
	{
		nbt.putFloat("current_energy", current_energy);
		nbt.putInt("energyregentimer", energyRegenTimer);
		nbt.putInt("energyticker", ticker);
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

	public float tryTransferEnergy(float maxAmount)
	{
		if (maxAmount >= current_energy)
		{
			float ret = current_energy;
			current_energy = 0;
			return ret;
		}
		else
		{
			current_energy -= maxAmount;
			return maxAmount;
		}
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
