package org.yang.interestingworld.client;

import net.fabricmc.api.ClientModInitializer;
import org.yang.interestingworld.client.entity.IWClientEntities;
import org.yang.interestingworld.client.network.IWClientNetwork;
import org.yang.interestingworld.client.particle.IWClientParticles;

public class InterestingworldClient implements ClientModInitializer
{

	@Override
	public void onInitializeClient()
	{
		IWClientNetwork.initialize();
		IWScreens.initialize();
		IWClientEntities.initialize();
		IWClientParticles.initialize();
		// EventManager.addListener(ThirstOverlayRenderer::onClientTick);
	}
}
