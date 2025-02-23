package org.yang.iw.datagen.property;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

public class IsBoostedBooleanProperty implements BooleanProperty
{
	public static final MapCodec<IsBoostedBooleanProperty> CODEC = MapCodec.unit(new IsBoostedBooleanProperty());

	@Override
	public boolean getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed,
							ModelTransformationMode modelTransformationMode)
	{
		return !stack.interestingWorld$getBoosts().isEmpty();
	}

	@Override
	public MapCodec<IsBoostedBooleanProperty> getCodec()
	{
		return CODEC;
	}
}

