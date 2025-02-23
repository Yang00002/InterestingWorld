package org.yang.iw.mixin.mixin_interface;

import org.yang.iw.entity.player.IWServerPlayerData;

public interface InterfaceServerPlayerEntity
{
	default IWServerPlayerData interestingWorld$getIWServerPlayerData()
	{
		return null;
	}
}
