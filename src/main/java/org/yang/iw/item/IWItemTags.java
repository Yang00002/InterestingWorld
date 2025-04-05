package org.yang.iw.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.RegistryCollector;
import org.yang.iw.api.tag.TagInclude;
import org.yang.iw.datagen.tag.ItemTagPool;
import org.yang.iw.util.Base;

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
	public static final TagKey<Item> SWORD = createTag("sword",
			new TagInclude<Item>().add(() -> Items.WOODEN_SWORD).add(() -> Items.STONE_SWORD)
					.add(() -> Items.GOLDEN_SWORD).add(() -> Items.DIAMOND_SWORD).add(() -> Items.NETHERITE_SWORD));

}