package org.yang.interestingworld.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.yang.interestingworld.client.network.IWClientNetwork;

public class InterestingworldClient implements ClientModInitializer
{

	@Override
	public void onInitializeClient()
	{
		IWClientNetwork.initialize();
		IWScreens.initialize();
		IWClientEntities.initialize();
		// EventManager.addListener(ThirstOverlayRenderer::onClientTick);
	}
}
