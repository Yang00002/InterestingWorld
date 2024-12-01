package org.yang.interestingworld;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.entity.dummy.DummyEntity;
import org.yang.interestingworld.util.Base;

public class IWEntities
{
	public static final EntityType<DummyEntity> DUMMY = Registry.register(Registries.ENTITY_TYPE,
			Identifier.of(Base.MOD_ID, "dummy"),
			FabricEntityType.Builder.createLiving(DummyEntity::new, SpawnGroup.MISC, (a) -> a).dimensions(0.6F, 1.8F)
					.build("dummy"));

	public static void initialize()
	{
		FabricDefaultAttributeRegistry.register(DUMMY, DummyEntity.createLivingAttributes());
	}
}
