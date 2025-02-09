package org.yang.iw.datagen.itemmodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.yang.iw.datagen.itemmodel.server.ModelParents;

public class SimpleItemModelProvider implements ItemModelProvider
{
	ModelParents model;
	Item item;

	public SimpleItemModelProvider(Item item, ModelParents model)
	{
		this.item = item;
		this.model = model;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void use(ItemModelGenerator generator)
	{
		generator.register(item, model.toClientModel());
	}

	@Override
	public Identifier getModelId()
	{
		Identifier identifier = Registries.ITEM.getId(item);
		return identifier.withPrefixedPath("item/");
	}
}
