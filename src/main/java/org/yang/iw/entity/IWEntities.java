package org.yang.iw.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.entity.dummy.DummyEntity;
import org.yang.iw.util.Base;

public class IWEntities
{
	public static final EntityType<DummyEntity> DUMMY = register("dummy", "测试假人",
			FabricEntityType.Builder.createLiving(DummyEntity::new, SpawnGroup.MISC, (a) -> a).dimensions(0.6F, 1.8F));

	public static <T extends Entity> EntityType<T> register(String id, String name, EntityType.Builder<T> entity)
	{
		var e = entity.build(id);
		Registry.register(Registries.ENTITY_TYPE, Identifier.of(Base.MOD_ID, id), e);
		TranslationPool.addEntity(e, name);
		return e;
	}

	public static void initialize()
	{
		FabricDefaultAttributeRegistry.register(DUMMY, DummyEntity.createLivingAttributes());
	}
}
