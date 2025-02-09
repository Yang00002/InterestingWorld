package org.yang.iw.block.block_builder;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.yang.iw.datagen.blockmodel.BlockModelPool;
import org.yang.iw.datagen.blockmodel.BlockModelProvider;
import org.yang.iw.datagen.blockmodel.ParentedBlockModelProvider;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.Base;

import java.util.function.Function;

public class CommonBlockBuilder implements BlockBuilder
{

	private final String id;
	private final Function<AbstractBlock.Settings, Block> constructer;
	private AbstractBlock.Settings settings = null;
	private String translation = null;

	private Function<Block, BlockModelProvider> modelProvider = null;

	public CommonBlockBuilder(Function<AbstractBlock.Settings, Block> constructer, String id)
	{
		this.constructer = constructer;
		this.id = id;
	}

	public CommonBlockBuilder setModel(Function<Block, BlockModelProvider> provider)
	{
		modelProvider = provider;
		return this;
	}

	public CommonBlockBuilder setParentModelForBlockAndItem(Block parent)
	{
		modelProvider = block -> new ParentedBlockModelProvider(block, parent).setModelForItem();
		return this;
	}

	public CommonBlockBuilder setTranslation(String str)
	{
		translation = str;
		return this;
	}

	public CommonBlockItemBuilder asItem()
	{
		return new CommonBlockItemBuilder(this, BlockItem::new, id);
	}

	public Block build()
	{
		var identifier = Base.getIWIdentifier(id);
		if (settings == null) settings = AbstractBlock.Settings.create();
		RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, identifier);
		Block block = Blocks.register(key, constructer, settings);
		if (modelProvider != null) BlockModelPool.addModel(modelProvider.apply(block));
		if (translation != null) TranslationPool.addBlock(block, translation);
		return block;
	}

}
