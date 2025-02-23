package org.yang.iw.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.DataGenSupplier;
import org.yang.iw.api.register.RegistryCollector;
import org.yang.iw.datagen.tag.ItemTagPool;
import org.yang.iw.util.Base;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@DataGenSupplier
@RegistryCollector
public class IWItemTags
{
	public static class Includes
	{
		List<Supplier<Item>> items = null;
		List<TagKey<Item>> tags = null;

		public Includes add(Supplier<Item> item)
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
			if (items != null) items.forEach(i -> itemConsumer.accept(i.get()));
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

	public static final TagKey<Item> IS_BASE_HEART = createTag("is_base_heart");

	public static void dataGenInitialize()
	{
	}
}