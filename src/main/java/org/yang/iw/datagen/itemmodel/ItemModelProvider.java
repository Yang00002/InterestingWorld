package org.yang.iw.datagen.itemmodel;

import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.util.Identifier;

public interface ItemModelProvider
{
	void use(ItemModelGenerator generator);

	Identifier getModelId();
}
