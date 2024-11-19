package org.yang.interestingworld.client.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.yang.interestingworld.playerdatamanager.ClientPlayerDataAccessor;
import org.yang.interestingworld.playerdatamanager.ClientPlayerDataManager;

@Mixin(ClientPlayerEntity.class)
public class ClientMixinClientPlayerEntity implements ClientPlayerDataAccessor
{
	@Unique
	private final ClientPlayerDataManager dataManager = new ClientPlayerDataManager();

	@Override
	public ClientPlayerDataManager getDataManager()
	{
		return dataManager;
	}
}
