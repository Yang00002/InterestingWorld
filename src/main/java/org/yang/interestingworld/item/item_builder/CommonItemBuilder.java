package org.yang.interestingworld.item.item_builder;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.util.Base;

import java.util.function.Function;

public class CommonItemBuilder
{

	private final Function<Item.Settings, Item> constructor;
	private final String id;
	private RegistryKey<ItemGroup> itemGroupBelong = null;

	public CommonItemBuilder(Function<Item.Settings, Item> constructor, String id)
	{
		this.constructor = constructor;
		this.id = id;
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
		return ret;
	}
}
