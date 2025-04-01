package org.yang.iw.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.datagen.tag.BoostTagPool;
import org.yang.iw.datagen.tag.ToolMaterialTagPool;
import org.yang.iw.tool.material.ToolMaterial;

import java.util.concurrent.CompletableFuture;

public class BoostTagGenerator extends FabricTagProvider<AbstractBoost>
{
	public BoostTagGenerator(FabricDataOutput output,
							 CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
	{
		super(output, IWRegistryKeys.BOOST, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
	{
		BoostTagPool.generatePool(this::getOrCreateTagBuilder);
	}
}
