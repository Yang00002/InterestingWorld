package org.yang.interestingworld.mixin.mixin_interface;

import org.yang.interestingworld.entity.player.IWServerPlayerData;

public interface InterfaceServerPlayerEntity
{
	default IWServerPlayerData getIWServerPlayerData()
	{
		return null;
	}
}
