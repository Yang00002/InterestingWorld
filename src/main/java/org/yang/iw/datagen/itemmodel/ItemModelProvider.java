package org.yang.iw.datagen.itemmodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelGenerator;

public interface ItemModelProvider extends ModelIdProvider
{
	@Environment(EnvType.CLIENT)
	void use(ItemModelGenerator generator);
}
