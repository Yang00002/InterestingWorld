package org.yang.iw.datagen.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.IWRegistries;
import org.yang.iw.component.IWComponents;
import org.yang.iw.component.MaterialPacketComponent;
import org.yang.iw.tool.material.ToolMaterial;

public record MaterialPacketSelectProperty(boolean overlay) implements SelectProperty<ToolMaterial>
{
	public static final Type<MaterialPacketSelectProperty, ToolMaterial> TYPE =
			Type.create(RecordCodecBuilder.mapCodec(
			(instance) -> instance.group(
							Codec.BOOL.optionalFieldOf("overlay", false).forGetter(MaterialPacketSelectProperty::overlay))
					.apply(instance, MaterialPacketSelectProperty::new)), IWRegistries.TOOL_MATERIAL.getCodec());

	@Nullable
	@Override
	public ToolMaterial getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed,
								 ModelTransformationMode modelTransformationMode)
	{
		var entry = stack.getOrDefault(IWComponents.MATERIAL_PACKET, MaterialPacketComponent.DEFAULT)
				.provideMaterial(overlay);
		if (entry == null) return null;
		return entry.material();
	}

	@Override
	public Type<? extends SelectProperty<ToolMaterial>, ToolMaterial> getType()
	{
		return TYPE;
	}
}

