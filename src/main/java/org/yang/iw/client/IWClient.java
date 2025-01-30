package org.yang.iw.client;

import net.fabricmc.api.ClientModInitializer;
import org.yang.iw.client.entity.IWClientEntities;
import org.yang.iw.client.network.IWClientNetwork;
import org.yang.iw.client.particle.IWClientParticles;

public class IWClient implements ClientModInitializer
{

	@Override
	public void onInitializeClient()
	{
		IWClientNetwork.initialize();
		IWScreens.initialize();
		IWClientEntities.initialize();
		IWClientParticles.initialize();
		IWKeyBindings.initialize();
	}
}
