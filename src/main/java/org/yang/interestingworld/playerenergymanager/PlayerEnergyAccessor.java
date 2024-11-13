package org.yang.interestingworld.playerenergymanager;

public interface PlayerEnergyAccessor
{
	float extractEnergy(float amount);

	PlayerEnergyManager getEnergyManager();
}
