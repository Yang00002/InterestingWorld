package org.yang.iw.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.datagen.tag.ToolMaterialTagPool;
import org.yang.iw.tool.material.ToolMaterial;

import java.util.concurrent.CompletableFuture;

public class ToolMaterialTagGenerator extends FabricTagProvider<ToolMaterial>
{
	public ToolMaterialTagGenerator(FabricDataOutput output,
									CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
	{
		super(output, IWRegistryKeys.TOOL_MATERIAL, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
	{
		ToolMaterialTagPool.generatePool(this::getOrCreateTagBuilder);
	}
}
