package org.yang.interestingworld.client;

import net.fabricmc.api.ClientModInitializer;
import org.yang.interestingworld.client.network.IWClientNetwork;

public class InterestingworldClient implements ClientModInitializer
{

	@Override
	public void onInitializeClient()
	{
		IWClientNetwork.initialize();
		IWScreens.initialize();
		// EventManager.addListener(ThirstOverlayRenderer::onClientTick);
	}
}
