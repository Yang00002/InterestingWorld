package org.yang.iw.item.item_builder;

import net.minecraft.data.client.Model;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.iw.IWItemGroups;
import org.yang.iw.datagen.itemmodel.CommonItemModelProvider;
import org.yang.iw.datagen.itemmodel.ItemModelProvider;
import org.yang.iw.datagen.itemmodel.ItemModelPool;
import org.yang.iw.util.Base;

import java.util.function.Function;

public class CommonItemBuilder
{

	private final Function<Item.Settings, Item> constructor;
	private final String id;
	private RegistryKey<ItemGroup> itemGroupBelong = null;
	private Function<Item, ItemModelProvider> modelProvider = null;

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

	public CommonItemBuilder setCommonModel(Model model)
	{
		modelProvider = item -> new CommonItemModelProvider(item, model);
		return this;
	}

	public CommonItemBuilder addToItemGroup(RegistryKey<ItemGroup> itemGroup)
	{
		itemGroupBelong = itemGroup;
		return this;
	}

	public Item build()
	{
		Item.Settings settings = new Item.Settings();
		Item ret = constructor.apply(settings);
		Identifier itemID = Identifier.of(Base.MOD_ID, id);
		Registry.register(Registries.ITEM, itemID, ret);
		if (itemGroupBelong != null)
		{
			IWItemGroups.addItemToGroup((context, entries) -> {
				var wrapperOp = context.lookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT);
				wrapperOp.ifPresent(wrapper -> entries.add(ret));
			}, itemGroupBelong);
		}
		if (modelProvider != null) ItemModelPool.addModel(modelProvider.apply(ret));
		return ret;
	}
}
