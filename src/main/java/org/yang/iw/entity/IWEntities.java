package org.yang.iw.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.entity.dummy.DummyEntity;
import org.yang.iw.util.Base;

@IndependentRegister
public class IWEntities
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
	}

	public static final EntityType<DummyEntity> DUMMY = register("dummy", "测试假人",
			FabricEntityType.Builder.createLiving(DummyEntity::new, SpawnGroup.MISC, (a) -> a).dimensions(0.6F, 1.8F));

	public static <T extends Entity> EntityType<T> register(String id, String name, EntityType.Builder<T> entity)
	{
		var idt = Identifier.of(Base.MOD_ID, id);
		var e = entity.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, idt));
		Registry.register(Registries.ENTITY_TYPE, idt, e);
		TranslationPool.addEntity(e, name);
		return e;
	}

	static
	{
		FabricDefaultAttributeRegistry.register(DUMMY, DummyEntity.createLivingAttributes());
	}

	public static void initialize()
	{
	}
}
