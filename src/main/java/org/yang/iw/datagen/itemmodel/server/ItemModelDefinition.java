package org.yang.iw.datagen.itemmodel.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import org.yang.iw.datagen.itemmodel.ModelIdProvider;
import org.yang.iw.datagen.util.AbilityToolIndexNumericProperty;
import org.yang.iw.datagen.util.HeartEnchantedBooleanProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ItemModelDefinition
{
	@Environment(EnvType.CLIENT)
	public abstract ItemModel.Unbaked toUnbaked();

	public static RangeItemModelDefinition abilityIndex(ItemModelDefinition defaultModelDefinition, Map<Float,
			ItemModelDefinition> predictors)
	{
		var m = new RangeItemModelDefinition.AbilityIndexType();
		m.defaultModelDefinition = defaultModelDefinition;
		m.predictors = predictors;
		return m;
	}

	public static BooleanItemModelDefinition heartEnchant(ItemModelDefinition t, ItemModelDefinition f)
	{
		var m = new BooleanItemModelDefinition.HeartEnchantType();
		m.onTrue = t;
		m.onFalse = f;
		return m;
	}


	public static abstract class RangeItemModelDefinition extends ItemModelDefinition
	{
		static class AbilityIndexType extends RangeItemModelDefinition
		{
			@Environment(EnvType.CLIENT)
			@Override
			public ItemModel.Unbaked toUnbaked()
			{
				List<RangeDispatchItemModel.Entry> entries = new LinkedList<>();
				predictors.forEach((i, j) -> entries.add(new RangeDispatchItemModel.Entry(i, j.toUnbaked())));
				return ItemModels.rangeDispatch(new AbilityToolIndexNumericProperty(),
						defaultModelDefinition.toUnbaked(), entries);
			}
		}

		Map<Float, ItemModelDefinition> predictors;

		ItemModelDefinition defaultModelDefinition;
	}

	public static abstract class BooleanItemModelDefinition extends ItemModelDefinition
	{


		private static class HeartEnchantType extends BooleanItemModelDefinition
		{
			@Environment(EnvType.CLIENT)
			@Override
			public ItemModel.Unbaked toUnbaked()
			{
				return ItemModels.condition(new HeartEnchantedBooleanProperty(), onTrue.toUnbaked(),
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
