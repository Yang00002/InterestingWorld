package org.yang.interestingworld.client;

import net.fabricmc.api.ClientModInitializer;
import org.yang.interestingworld.client.network.IWClientNetwork;

public class InterestingworldClient implements ClientModInitializer
{

    private float totalTickDelta = 0f;

    @Override
    public void onInitializeClient()
    {
        IWClientNetwork.initialize();
        IWScreens.initialize();
    }
}
