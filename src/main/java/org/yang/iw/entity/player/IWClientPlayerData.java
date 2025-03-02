package org.yang.iw.entity.player;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.network.bitio.BitReader;
import org.yang.iw.network.payload.C2SAbilityKeyPressPayload;

public class IWClientPlayerData
{
	public boolean server_ability_on_opt;
	private int cooldownStep = 0;
	private AbilityBarType barType = AbilityBarType.EMPTY;
	private int[] registers = new int[16];
	private int tickStep = 0;
	private boolean abilityOn = false;
	private boolean client_ability_on = true;
	public AbstractAbility WeaponAbility = AbstractAbility.getDefault();
	private int shown_energy = 0;
	public int charge_rate16 = 0;
	public int shown_number = 0;
	public boolean is_charged = false;

	public void handleS2CInitializeDataBuffer(BitReader buf)
	{
		client_ability_on = buf.readBoolean();
	}

	public void switchClientAbilityOpen()
	{
		client_ability_on = !client_ability_on;
		ClientPlayNetworking.send(new C2SAbilityKeyPressPayload(client_ability_on));
	}

	public int loadRegister(int i)
	{
		return i >= 16 ? 0 : registers[i];
	}

	public void setRegister(int i, int value)
	{
		if (i >= 16) return;
		registers[i] = value;
	}

	public void setCooldown(int step)
	{
		cooldownStep = step;
	}


	public void setTickStep(int step)
	{
		tickStep = step;
	}

	public void setBarType(AbilityBarType type)
	{
		barType = type;
	}

	public AbilityBarType abilityBarType()
	{
		return barType;
	}

	public int tickStep()
	{
		return tickStep;
	}

	public void setNotCooldown()
	{
		cooldownStep = 16;
	}

	public boolean isCooldown()
	{
		return cooldownStep < 16;
	}

	public int cooldownProgress()
	{
		return cooldownStep;
	}

	public void setAbilityOn(boolean isOn)
	{
		abilityOn = isOn;
	}

	public boolean isAbilityOn()
	{
		return abilityOn;
	}

	public void setEnergy(int energy)
	{
		shown_energy = energy;
	}

	public int getEnergy()
	{
		return shown_energy;
	}
}
