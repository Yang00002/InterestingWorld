package org.yang.iw.datagen.blockmodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.util.Identifier;

public interface BlockModelProvider
{
	@Environment(EnvType.CLIENT)
	void use(BlockStateModelGenerator generator);

	@Environment(EnvType.CLIENT)
	void useForItem(BlockStateModelGenerator generator);

	Identifier getModelId();
}
