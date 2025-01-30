package org.yang.iw.datagen.blockmodel;

import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.util.Identifier;

public interface BlockModelProvider
{
	void use(BlockStateModelGenerator generator);

	void useForItem(BlockStateModelGenerator generator);

	Identifier getModelId();
}
