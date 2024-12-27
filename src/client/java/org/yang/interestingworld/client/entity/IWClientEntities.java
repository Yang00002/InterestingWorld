package org.yang.interestingworld.client.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.yang.interestingworld.entity.IWEntities;
import org.yang.interestingworld.client.entity.dummy.DummyEntityRenderer;

public class IWClientEntities
{
	public static void initialize()
	{
		EntityRendererRegistry.register(IWEntities.DUMMY, DummyEntityRenderer::new);
	}
}
