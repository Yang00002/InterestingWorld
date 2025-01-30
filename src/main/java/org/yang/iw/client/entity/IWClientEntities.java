package org.yang.iw.client.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.yang.iw.entity.IWEntities;
import org.yang.iw.client.entity.dummy.DummyEntityRenderer;

public class IWClientEntities
{
	public static void initialize()
	{
		EntityRendererRegistry.register(IWEntities.DUMMY, DummyEntityRenderer::new);
	}
}
