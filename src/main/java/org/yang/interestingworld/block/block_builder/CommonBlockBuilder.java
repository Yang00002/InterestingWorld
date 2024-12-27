package org.yang.interestingworld.block.block_builder;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.yang.interestingworld.util.Base;

import java.util.function.Function;

public class CommonBlockBuilder implements BlockBuilder
{

	private final String id;
	private final Function<AbstractBlock.Settings, Block> constructer;
	private AbstractBlock.Settings settings = null;

	public CommonBlockBuilder(Function<AbstractBlock.Settings, Block> constructer, String id)
	{
		this.constructer = constructer;
		this.id = id;
	}

	public CommonBlockItemBuilder asItem()
	{
		return new CommonBlockItemBuilder(this, BlockItem::new, id);
	}

	public Block build()
	{
		if (settings == null) settings = AbstractBlock.Settings.create();
		Block block = constructer.apply(settings);
		Registry.register(Registries.BLOCK, Base.getIWIdentifier(id), block);
		return block;
	}

}
