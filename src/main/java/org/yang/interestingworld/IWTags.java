package org.yang.interestingworld;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class IWTags
{
	public static class EnergyToolTypeTags
	{
		public static final TagKey<Item> ROD = TagKey.of(RegistryKeys.ITEM,
				Identifier.of(IWUtil.Base.MOD_ID, "tooltype/rod"));
		public static final TagKey<Item> SWORD = TagKey.of(RegistryKeys.ITEM,
				Identifier.of(IWUtil.Base.MOD_ID, "tooltype/sword"));

		public static List<TagKey<Item>> getAll()
		{
			List<TagKey<Item>> list = new LinkedList<>();
			list.add(ROD);
			list.add(SWORD);
			return list;
		}

		public static void initialize()
		{

		}
	}

	public static void initialize()
	{
		EnergyToolTypeTags.initialize();
	}
}
