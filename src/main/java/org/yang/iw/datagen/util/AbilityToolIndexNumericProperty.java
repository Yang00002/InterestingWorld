package org.yang.iw.datagen.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static org.yang.iw.util.IWRuneAbilityUtil.getAbility;

public record AbilityToolIndexNumericProperty() implements NumericProperty
{
	public static final MapCodec<AbilityToolIndexNumericProperty> CODEC = MapCodec.unit(new AbilityToolIndexNumericProperty());

	@Override
	public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed)
	{
		return getAbility(stack).toolIndex;
	}

	@Override
	public MapCodec<AbilityToolIndexNumericProperty> getCodec()
	{
		return CODEC;
	}
}

