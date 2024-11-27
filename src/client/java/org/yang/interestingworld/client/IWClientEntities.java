package org.yang.interestingworld.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWEntities;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.client.entity.dummy.DummyEntityModel;
import org.yang.interestingworld.client.entity.dummy.DummyEntityRenderer;

public class IWClientEntities
{
	public static void initialize()
	{
		EntityRendererRegistry.register(IWEntities.DUMMY, DummyEntityRenderer::new);
	}
}
