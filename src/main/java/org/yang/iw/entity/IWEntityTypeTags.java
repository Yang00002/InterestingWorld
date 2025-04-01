package org.yang.iw.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.RegistryCollector;
import org.yang.iw.datagen.tag.EntityTypeTagPool;
import org.yang.iw.util.Base;
import org.yang.iw.api.tag.TagInclude;

@RegistryCollector
public class IWEntityTypeTags
{

	public static TagKey<EntityType<?>> HAVE_BIG_NOSE = createTag("have_big_nose",
			new TagInclude<EntityType<?>>().add(EntityTypeTags.ILLAGER).add(() -> EntityType.WANDERING_TRADER)
					.add(() -> EntityType.VILLAGER).add(() -> EntityType.IRON_GOLEM).add(() -> EntityType.RAVAGER)
					.add(() -> EntityType.WITCH).add(() -> EntityType.ZOMBIE_VILLAGER));

	private static TagKey<EntityType<?>> createTag(String id)
	{
		return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Base.MOD_ID, id));
	}

	private static TagKey<EntityType<?>> createTag(String id, TagInclude<EntityType<?>> includes)
	{
		var ret = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Base.MOD_ID, id));
		includes.map(item -> EntityTypeTagPool.add(ret, item), tag -> EntityTypeTagPool.add(ret, tag));
		return ret;
	}
}
