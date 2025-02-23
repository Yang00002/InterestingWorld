package org.yang.iw.datagen.itemmodel.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.datagen.itemmodel.ModelIdProvider;
import org.yang.iw.datagen.property.AbilitySelectProperty;
import org.yang.iw.datagen.property.IsBoostedBooleanProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ItemModelDefinition
{
	@Environment(EnvType.CLIENT)
	public abstract ItemModel.Unbaked toUnbaked();

	public static SelectItemModelDefinition ability(ItemModelDefinition defaultModelDefinition, Map<AbstractAbility,
			ItemModelDefinition> predictors)
	{
		var m = new SelectItemModelDefinition.AbilityType();
		m.defaultModelDefinition = defaultModelDefinition;
		Map<AbstractAbility, ItemModelDefinition> np = new HashMap<>();
		predictors.forEach((ability, model) -> {
			var p = ability.getToolRenderAbility();
			if (p != null) np.put(p, model);
		});
		m.predictors = np;
		return m;
	}

	public static BooleanItemModelDefinition isBoosted(ItemModelDefinition t, ItemModelDefinition f)
	{
		var m = new BooleanItemModelDefinition.IsBoostedType();
		m.onTrue = t;
		m.onFalse = f;
		return m;
	}


	public static abstract class RangeItemModelDefinition extends ItemModelDefinition
	{
		Map<Float, ItemModelDefinition> predictors;

		ItemModelDefinition defaultModelDefinition;
	}

	public static abstract class SelectItemModelDefinition extends ItemModelDefinition
	{
		static class AbilityType extends SelectItemModelDefinition
		{
			@Environment(EnvType.CLIENT)
			@Override
			public ItemModel.Unbaked toUnbaked()
			{
				List<SelectItemModel.SwitchCase<AbstractAbility>> entries = new LinkedList<>();
				predictors.forEach((i, j) -> entries.add(new SelectItemModel.SwitchCase<>(List.of(i), j.toUnbaked())));
				return ItemModels.select(new AbilitySelectProperty(), defaultModelDefinition.toUnbaked(), entries);
			}
		}

		Map<AbstractAbility, ItemModelDefinition> predictors;

		ItemModelDefinition defaultModelDefinition;
	}

	public static abstract class BooleanItemModelDefinition extends ItemModelDefinition
	{


		private static class IsBoostedType extends BooleanItemModelDefinition
		{
			@Environment(EnvType.CLIENT)
			@Override
			public ItemModel.Unbaked toUnbaked()
			{
				return ItemModels.condition(new IsBoostedBooleanProperty(), onTrue.toUnbaked(),
						onFalse.toUnbaked());
			}
		}

		ItemModelDefinition onTrue;
		ItemModelDefinition onFalse;
	}

	public static SimpleItemModelDefinition of(ModelIdProvider model)
	{
		return new SimpleItemModelDefinition(model);
	}

	public static class SimpleItemModelDefinition extends ItemModelDefinition
	{
		ModelIdProvider model;

		private SimpleItemModelDefinition(ModelIdProvider model)
		{
			this.model = model;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public ItemModel.Unbaked toUnbaked()
		{
			return ItemModels.basic(model.getModelId());
		}
	}
}
