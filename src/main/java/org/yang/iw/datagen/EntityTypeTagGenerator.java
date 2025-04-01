package org.yang.iw.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import org.yang.iw.datagen.tag.EntityTypeTagPool;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagGenerator extends FabricTagProvider<EntityType<?>>
{
	public EntityTypeTagGenerator(FabricDataOutput output,
								  CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
	{
		super(output, RegistryKeys.ENTITY_TYPE, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup)
	{
		EntityTypeTagPool.generatePool(this::getOrCreateTagBuilder);
	}
}
