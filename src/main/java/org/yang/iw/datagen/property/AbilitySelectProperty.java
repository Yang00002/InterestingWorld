package org.yang.iw.datagen.property;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.IWRegistries;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.component.AbilityComponent;
import org.yang.iw.component.IWComponents;

public record AbilitySelectProperty() implements SelectProperty<AbstractAbility>
{
	public static final SelectProperty.Type<AbilitySelectProperty, AbstractAbility> TYPE = SelectProperty.Type.create(
			MapCodec.unit(new AbilitySelectProperty()), IWRegistries.ABILITY.getCodec());

	@Nullable
	@Override
	public AbstractAbility getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user,
									int seed, ModelTransformationMode modelTransformationMode)
	{
		var ret = stack.getOrDefault(IWComponents.ABILITY, AbilityComponent.DEFAULT).ability().getToolRenderAbility();
		return ret == null ? AbstractAbility.getDefault() : ret;
	}

	@Override
	public Type<? extends SelectProperty<AbstractAbility>, AbstractAbility> getType()
	{
		return TYPE;
	}
}

