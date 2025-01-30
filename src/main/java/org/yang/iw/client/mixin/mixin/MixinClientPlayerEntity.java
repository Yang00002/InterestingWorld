package org.yang.iw.client.mixin.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.yang.iw.mixin.mixin_interface.InterfaceClientPlayerEntity;
import org.yang.iw.entity.player.IWClientPlayerData;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity implements InterfaceClientPlayerEntity
{
	@Unique
	private final IWClientPlayerData dataManager = new IWClientPlayerData();

	@Override
	@Unique
	public IWClientPlayerData getIWClientPlayerData()
	{
		return dataManager;
	}
}
