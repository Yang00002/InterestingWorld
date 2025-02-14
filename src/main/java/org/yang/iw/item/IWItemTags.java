package org.yang.iw.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.datagen.tag.ItemTagPool;
import org.yang.iw.util.Base;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IWItemTags
{
	public static class Includes
	{
		List<Item> items = null;
		List<TagKey<Item>> tags = null;

		public Includes add(Item item)
		{
			if (items == null) items = new ArrayList<>();
			items.add(item);
			return this;
		}

		public Includes add(TagKey<Item> tag)
		{
			if (tags == null) tags = new ArrayList<>();
			tags.add(tag);
			return this;
		}

		public void map(Consumer<Item> itemConsumer, Consumer<TagKey<Item>> tagKeyConsumer)
		{
			if (items != null) items.forEach(itemConsumer);
			if (tags != null) tags.forEach(tagKeyConsumer);
		}
	}

	private static TagKey<Item> createTag(String id)
	{
		return TagKey.of(RegistryKeys.ITEM, Identifier.of(Base.MOD_ID, id));
	}

	private static TagKey<Item> createTag(String id, Includes includes)
	{
		var ret = TagKey.of(RegistryKeys.ITEM, Identifier.of(Base.MOD_ID, id));
		includes.map(item -> ItemTagPool.add(ret, item), tag -> ItemTagPool.add(ret, tag));
		return ret;
	}

	public static final TagKey<Item> CanEnchantAsPreEnchantHeart = createTag("can_pre_enchant");
	public static final TagKey<Item> CanRepairBlazeRod = createTag("can_repair_blazerod",
			new Includes().add(Items.BLAZE_ROD).add(Items.BLAZE_POWDER));

	public static void initialize()
	{

	}
}