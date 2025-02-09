package org.yang.iw.item.item_builder;

import net.minecraft.client.data.Model;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.IWItemGroups;
import org.yang.iw.datagen.itemmodel.ItemModelPool;
import org.yang.iw.datagen.itemmodel.ItemModelProvider;
import org.yang.iw.datagen.itemmodel.SimpleItemModelProvider;
import org.yang.iw.datagen.itemmodel.server.ModelParents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.datagen.tag.ItemTagPool;
import org.yang.iw.util.Base;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;


public class CommonItemBuilder
{

	private final Function<Item.Settings, Item> constructor;
	private final String id;
	private RegistryKey<ItemGroup> itemGroupBelong = null;
	private Function<Item, ItemModelProvider> modelProvider = null;
	private String translation = null;
	private final Set<TagKey<Item>> tags = new HashSet<>();

	public CommonItemBuilder(Function<Item.Settings, Item> constructor, String id)
	{
		this.constructor = constructor;
		this.id = id;
	}

	public CommonItemBuilder setModel(Function<Item, ItemModelProvider> provider)
	{
		modelProvider = provider;
		return this;
	}

	public CommonItemBuilder setCommonModel(ModelParents model)
	{
		modelProvider = item -> new SimpleItemModelProvider(item, model);
		return this;
	}

	public CommonItemBuilder addToItemGroup(RegistryKey<ItemGroup> itemGroup)
	{
		itemGroupBelong = itemGroup;
		return this;
	}

	public CommonItemBuilder setTranslation(String str)
	{
		translation = str;
		return this;
	}

	public CommonItemBuilder addTag(TagKey<Item> tag)
	{
		tags.add(tag);
		return this;
	}

	public Item build()
	{

		Item.Settings settings = new Item.Settings();
		Identifier itemID = Identifier.of(Base.MOD_ID, id);
		RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, itemID);
		Item ret = Items.register(registryKey, constructor, settings);
		if (itemGroupBelong != null)
		{
			IWItemGroups.addItemToGroup((context, entries) -> {
				var wrapperOp = context.lookup().getOptional(RegistryKeys.ENCHANTMENT);
				wrapperOp.ifPresent(wrapper -> entries.add(ret));
			}, itemGroupBelong);
		}
		if (modelProvider != null) ItemModelPool.addModel(modelProvider.apply(ret));
		if (translation != null) TranslationPool.addItem(ret, translation);
		tags.forEach(tag -> ItemTagPool.add(tag, ret));
		return ret;
	}
}
