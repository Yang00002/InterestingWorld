package org.yang.interestingworld.block.block_builder;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWItemGroups;
import org.yang.interestingworld.util.Base;

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
		Item item = constructor.apply(block, settings);
		Identifier itemID = Identifier.of(Base.MOD_ID, id);
		Registry.register(Registries.ITEM, itemID, item);
		if (itemGroupBelong != null)
		{
			IWItemGroups.addItemToGroup((context, entries) -> {
				var wrapperOp = context.lookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT);
				wrapperOp.ifPresent(wrapper -> entries.add(item));
			}, itemGroupBelong);
		}
		return block;
	}
}
