package org.yang.interestingworld.playerdatamanager;

public interface PlayerMixinAccessor
{
	float extractEnergy(float amount);

	PlayerDataManager getDataManager();
}
