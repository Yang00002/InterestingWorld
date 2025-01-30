package org.yang.iw.datagen.blockmodel;

import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

public class ParentedBlockModelProvider implements BlockModelProvider
{
	Block parent;
	Block block;
	boolean forParent = false;

	public ParentedBlockModelProvider(Block block, Block parent)
	{
		this.block = block;
		this.parent = parent;
	}

	@Override
	public void use(BlockStateModelGenerator generator)
	{
		generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block,
				BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(parent))));
		if (forParent) useForItem(generator);
	}

	public ParentedBlockModelProvider setModelForItem()
	{
		forParent = true;
		return this;
	}

	@Override
	public void useForItem(BlockStateModelGenerator generator)
	{
		generator.registerParentedItemModel(block, ModelIds.getBlockModelId(parent));
	}

	@Override
	public Identifier getModelId()
	{
		return ModelIds.getBlockModelId(block);
	}
}
