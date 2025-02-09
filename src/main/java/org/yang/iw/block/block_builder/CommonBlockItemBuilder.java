package org.yang.iw.block.block_builder;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.iw.IWItemGroups;
import org.yang.iw.util.Base;

import java.util.function.BiFunction;

public class CommonBlockItemBuilder
{
	private final BlockBuilder blockBuilder;
	private final BiFunction<Block, Item.Settings, Item> constructor;
	private final String id;
	private RegistryKey<ItemGroup> itemGroupBelong = IWItemGroups.BLOCKS_GROUP;

	CommonBlockItemBuilder(BlockBuilder blockBuilder, BiFunction<Block, Item.Settings, Item> constructor, String id)
	{
		this.blockBuilder = blockBuilder;
		this.constructor = constructor;
		this.id = id;
	}

	public CommonBlockItemBuilder addToItemGroup(RegistryKey<ItemGroup> itemGroup)
	{
		itemGroupBelong = itemGroup;
		return this;
	}

	public Block build()
	{
		Block block = blockBuilder.build();
		Item.Settings settings = new Item.Settings();
		Identifier itemID = Identifier.of(Base.MOD_ID, id);
		var key = RegistryKey.of(RegistryKeys.ITEM, itemID);
		settings.registryKey(key);
		Item item = constructor.apply(block, settings);
		Registry.register(Registries.ITEM, key, item);
		if (itemGroupBelong != null)
		{
			IWItemGroups.addItemToGroup((context, entries) -> {
				var wrapperOp = context.lookup().getOptional(RegistryKeys.ENCHANTMENT);
				wrapperOp.ifPresent(wrapper -> entries.add(item));
			}, itemGroupBelong);
		}
		return block;
	}
}
