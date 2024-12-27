package org.yang.interestingworld.mixin.mixin_interface;

import org.yang.interestingworld.entity.player.IWClientPlayerData;

public interface InterfaceClientPlayerEntity
{
	default IWClientPlayerData getIWClientPlayerData()
	{
		return null;
	}
}
