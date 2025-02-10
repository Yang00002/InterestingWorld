package org.yang.iw.datagen.itemmodel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ModelIds;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.yang.iw.datagen.itemmodel.server.ModelParents;

import static org.yang.iw.util.Base.iwlogger;

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
		iwlogger.warn("register " + getModelId());
		generator.register(item, model.toClientModel());
	}

	@Override
	public Identifier getModelId()
	{
		Identifier identifier = Registries.ITEM.getId(item);
		return identifier.withPrefixedPath("item/");
	}
}
