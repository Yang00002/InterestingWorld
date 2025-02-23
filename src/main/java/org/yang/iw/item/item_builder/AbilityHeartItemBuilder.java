package org.yang.iw.item.item_builder;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
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
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.util.Base;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AbilityHeartItemBuilder
{

	private final BiFunction<Item.Settings, Identifier, AbilityHeart> constructor;
	private final String id;
	private RegistryKey<ItemGroup> itemGroupBelong = null;
	private Function<Item, ItemModelProvider> modelProvider = null;
	private String translation = null;
	private final Set<TagKey<Item>> tags = new HashSet<>();

	public AbilityHeartItemBuilder addTag(TagKey<Item> tag)
	{
		tags.add(tag);
		return this;
	}

	public AbilityHeartItemBuilder setModel(Function<Item, ItemModelProvider> provider)
	{
		modelProvider = provider;
		return this;
	}

	public AbilityHeartItemBuilder setTranslation(String str)
	{
		translation = str;
		return this;
	}

	public AbilityHeartItemBuilder setCommonModel(ModelParents model)
	{
		modelProvider = item -> new SimpleItemModelProvider(item, model);
		return this;
	}

	public AbilityHeartItemBuilder(BiFunction<Item.Settings, Identifier, AbilityHeart> constructor, String id)
	{
		this.constructor = constructor;
		this.id = id;
	}

	public AbilityHeartItemBuilder addToItemGroup(RegistryKey<ItemGroup> itemGroup)
	{
		itemGroupBelong = itemGroup;
		return this;
	}

	public AbilityHeart build()
	{
		Item.Settings settings = new Item.Settings();
		Identifier itemID = Identifier.of(Base.MOD_ID, id);
		RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, itemID);
		AbilityHeart ret = (AbilityHeart) Items.register(registryKey,
				settings1 -> constructor.apply(settings1, itemID), settings);
		if (itemGroupBelong != null)
			IWItemGroups.addItemToGroup((context, entries) -> entries.add(ret.getDefaultStack()), itemGroupBelong);
		if (modelProvider != null) ItemModelPool.addModel(() -> modelProvider.apply(ret));
		if (translation != null) TranslationPool.addItem(ret, translation);
		tags.forEach(tag -> ItemTagPool.add(tag, ret));
		return ret;
	}
}
