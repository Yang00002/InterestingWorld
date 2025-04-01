package org.yang.iw.datagen.itemmodel.server;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.render.item.model.EmptyItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.datagen.itemmodel.ModelIdProvider;
import org.yang.iw.datagen.property.AbilitySelectProperty;
import org.yang.iw.datagen.property.IsBoostedBooleanProperty;
import org.yang.iw.datagen.property.MaterialPacketSelectProperty;
import org.yang.iw.datagen.property.ToolMaterialSelectProperty;
import org.yang.iw.tool.material.IWToolMaterials;
import org.yang.iw.tool.material.ToolMaterial;

import java.util.*;

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

	public static SelectItemModelDefinition materialPacket(boolean overlay, ItemModelDefinition defaultModelDefinition
			, Map<ToolMaterial, ItemModelDefinition> predictors)
	{
		var m = new SelectItemModelDefinition.MaterialPacketType(overlay);
		m.defaultModelDefinition = defaultModelDefinition;
		m.predictors = new HashMap<>(predictors);
		return m;
	}

	public static SelectItemModelDefinition toolMaterial(int materialIndex, ItemModelDefinition defaultModelDefinition
			, Map<ToolMaterial, ItemModelDefinition> predictors)
	{
		var m = new SelectItemModelDefinition.ToolMaterialType(materialIndex);
		m.defaultModelDefinition = defaultModelDefinition;
		m.predictors = new HashMap<>(predictors);
		return m;
	}

	public static EmptyItemModelDefinition empty()
	{
		return new EmptyItemModelDefinition();
	}

	public static BooleanItemModelDefinition isBoosted(ItemModelDefinition t, ItemModelDefinition f)
	{
		var m = new BooleanItemModelDefinition.IsBoostedType();
		m.onTrue = t;
		m.onFalse = f;
		return m;
	}

	public static CompositeItemModelDefinition composite(ItemModelDefinition background,
														 ItemModelDefinition... definitions)
	{
		var m = new CompositeItemModelDefinition();
		m.itemModelDefinitions = new ObjectArrayList<>();
		m.itemModelDefinitions.add(background);
		m.itemModelDefinitions.addAll(Arrays.asList(definitions));
		return m;
	}

	public static ItemModelDefinition compositeTool(Map<ToolMaterial, ItemModelDefinition> d1, Map<ToolMaterial,
			ItemModelDefinition> d2)
	{
		var i1 = toolMaterial(0, d1.getOrDefault(IWToolMaterials.WOOD, null), d1);
		var i2 = toolMaterial(1, d2.getOrDefault(IWToolMaterials.WOOD, null), d2);
		var m = new CompositeItemModelDefinition();
		m.itemModelDefinitions = new ObjectArrayList<>();
		m.itemModelDefinitions.add(i1);
		m.itemModelDefinitions.add(i2);
		return m;
	}

	public static class EmptyItemModelDefinition extends ItemModelDefinition
	{
		@Override
		public ItemModel.Unbaked toUnbaked()
		{
			return new EmptyItemModel.Unbaked();
		}
	}


	public static abstract class RangeItemModelDefinition extends ItemModelDefinition
	{
		Map<Float, ItemModelDefinition> predictors;

		ItemModelDefinition defaultModelDefinition;
	}

	public static class CompositeItemModelDefinition extends ItemModelDefinition
	{
		List<ItemModelDefinition> itemModelDefinitions;

		@Override
		public ItemModel.Unbaked toUnbaked()
		{
			int size = itemModelDefinitions.size();
			ItemModel.Unbaked[] models = new ItemModel.Unbaked[size];
			for (int i = 0; i < size; i++)
				models[i] = itemModelDefinitions.get(i).toUnbaked();
			return ItemModels.composite(models);
		}
	}

	public static abstract class SelectItemModelDefinition extends ItemModelDefinition
	{
		static class AbilityType extends SelectItemModelDefinition
		{

			Map<AbstractAbility, ItemModelDefinition> predictors;

			@Environment(EnvType.CLIENT)
			@Override
			public ItemModel.Unbaked toUnbaked()
			{
				List<SelectItemModel.SwitchCase<AbstractAbility>> entries = new LinkedList<>();
				predictors.forEach((i, j) -> entries.add(new SelectItemModel.SwitchCase<>(List.of(i), j.toUnbaked())));
				return ItemModels.select(new AbilitySelectProperty(), defaultModelDefinition.toUnbaked(), entries);
			}
		}

		static class MaterialPacketType extends SelectItemModelDefinition
		{
			boolean overlay;

			Map<ToolMaterial, ItemModelDefinition> predictors;

			MaterialPacketType(boolean o)
			{
				overlay = o;
			}

			@Environment(EnvType.CLIENT)
			@Override
			public ItemModel.Unbaked toUnbaked()
			{
				List<SelectItemModel.SwitchCase<ToolMaterial>> entries = new LinkedList<>();
				predictors.forEach((i, j) -> entries.add(new SelectItemModel.SwitchCase<>(List.of(i), j.toUnbaked())));
				return ItemModels.select(new MaterialPacketSelectProperty(overlay), defaultModelDefinition.toUnbaked(),
						entries);
			}
		}

		static class ToolMaterialType extends SelectItemModelDefinition
		{

			ToolMaterialType(int idx)
			{
				index = idx;
			}

			int index;
			Map<ToolMaterial, ItemModelDefinition> predictors;

			@Environment(EnvType.CLIENT)
			@Override
			public ItemModel.Unbaked toUnbaked()
			{
				List<SelectItemModel.SwitchCase<ToolMaterial>> entries = new LinkedList<>();
				predictors.forEach((i, j) -> entries.add(new SelectItemModel.SwitchCase<>(List.of(i), j.toUnbaked())));
				return ItemModels.select(new ToolMaterialSelectProperty(index), defaultModelDefinition.toUnbaked(),
						entries);
			}
		}

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
				return ItemModels.condition(new IsBoostedBooleanProperty(), onTrue.toUnbaked(), onFalse.toUnbaked());
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
