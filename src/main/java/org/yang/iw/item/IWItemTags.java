package org.yang.iw.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.RegistryCollector;
import org.yang.iw.datagen.tag.ItemTagPool;
import org.yang.iw.util.Base;
import org.yang.iw.api.tag.TagInclude;

@RegistryCollector
public class IWItemTags
{

	private static TagKey<Item> createTag(String id)
	{
		return TagKey.of(RegistryKeys.ITEM, Identifier.of(Base.MOD_ID, id));
	}

	private static TagKey<Item> createTag(String id, TagInclude<Item> includes)
	{
		var ret = TagKey.of(RegistryKeys.ITEM, Identifier.of(Base.MOD_ID, id));
		includes.map(item -> ItemTagPool.add(ret, item), tag -> ItemTagPool.add(ret, tag));
		return ret;
	}

	public static final TagKey<Item> IS_BASE_HEART = createTag("is_base_heart");

}