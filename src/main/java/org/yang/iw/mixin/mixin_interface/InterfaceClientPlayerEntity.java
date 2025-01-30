package org.yang.iw.mixin.mixin_interface;

import org.yang.iw.entity.player.IWClientPlayerData;

public interface InterfaceClientPlayerEntity
{
	default IWClientPlayerData getIWClientPlayerData()
	{
		return null;
	}
}
