package org.yang.iw.datagen.property;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.IWRegistries;
import org.yang.iw.component.IWComponents;
import org.yang.iw.component.ToolMaterialComponent;
import org.yang.iw.tool.material.ToolMaterial;

public record ToolMaterialSelectProperty(int index) implements SelectProperty<ToolMaterial>
{
	public static final Type<ToolMaterialSelectProperty, ToolMaterial> TYPE = Type.create(RecordCodecBuilder.mapCodec(
			(instance) -> instance.group(
							Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(ToolMaterialSelectProperty::index))
					.apply(instance, ToolMaterialSelectProperty::new)), IWRegistries.TOOL_MATERIAL.getCodec());

	@Nullable
	@Override
	public ToolMaterial getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed,
								 ModelTransformationMode modelTransformationMode)
	{
		return stack.getOrDefault(IWComponents.TOOL_MATERIAL, ToolMaterialComponent.DEFAULT).at(index);
	}

	@Override
	public Type<? extends SelectProperty<ToolMaterial>, ToolMaterial> getType()
	{
		return TYPE;
	}
}

