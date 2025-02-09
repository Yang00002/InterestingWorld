package org.yang.iw.datagen.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.util.heartflag.HeartDataFlag;
import org.yang.iw.util.heartflag.HeartFlagOnlyCheckable;

public class HeartEnchantedBooleanProperty implements BooleanProperty
{
	public static final MapCodec<HeartEnchantedBooleanProperty> CODEC = MapCodec.unit(
			new HeartEnchantedBooleanProperty());

	@Override
	public boolean getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed,
							ModelTransformationMode modelTransformationMode)
	{
		return HeartDataFlag.getFromItemStack(stack).getTypeTaking() == HeartFlagOnlyCheckable.HeartTypeTaking.ENCHANT;
	}

	@Override
	public MapCodec<HeartEnchantedBooleanProperty> getCodec()
	{
		return CODEC;
	}
}

