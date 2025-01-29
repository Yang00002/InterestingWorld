package org.yang.interestingworld.datagen.itemmodel;

import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class CommonItemModelProvider implements ItemModelProvider
{
	Model model;
	Item item;

	public CommonItemModelProvider(Item item, Model model)
	{
		this.item = item;
		this.model = model;
	}

	@Override
	public void use(ItemModelGenerator generator)
	{
		generator.register(item, model);
	}

	@Override
	public Identifier getModelId()
	{
		return ModelIds.getItemModelId(item);
	}
}
