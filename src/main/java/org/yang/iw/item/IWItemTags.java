package org.yang.iw.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.util.Base;

public class IWItemTags
{
	private static TagKey<Item> createTag(String id)
	{
		return TagKey.of(RegistryKeys.ITEM, Identifier.of(Base.MOD_ID, id));
	}

	public static final TagKey<Item> CanEnchantAsPreEnchantHeart = createTag("can_pre_enchant");

	public static void initialize()
	{

	}
}