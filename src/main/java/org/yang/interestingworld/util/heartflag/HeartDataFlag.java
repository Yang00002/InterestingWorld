package org.yang.interestingworld.util.heartflag;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.yang.interestingworld.IWComponents;

public class HeartDataFlag implements HeartFlagOnlyCheckable
{
	private static final HeartDataFlag EMPTY = new HeartDataFlag(0);
	private int flag;

	public static Codec<HeartDataFlag> CODEC = Codec.INT.xmap(HeartDataFlag::new, HeartDataFlag::getValue);
	/**
	 * 材料等级 bit 3 位, 在 0 - 7 之间, 代表 1 - 8.
	 */
	private static final int MATERIAL_LEVEL_BITS = 0b0111;

	private static final int TYPE_TAKING_BITS = 0b011000;
	private static final int TAKING_LEVEL_BITS = 0b0111100000;

	HeartDataFlag(int i)
	{
		flag = i;
	}

	@Override
	public int getValue()
	{
		return flag;
	}

	@Override
	public int getMaterialLevel()
	{
		return (MATERIAL_LEVEL_BITS & flag) + 1;
	}

	@Override
	public int getTakingLevel()
	{
		return (flag & TAKING_LEVEL_BITS) >> 5;
	}

	public HeartDataFlag setTakingLevel(int lvl)
	{
		flag = (flag & ~TAKING_LEVEL_BITS) | ((Math.clamp(lvl, 0, 8)) << 5);
		return this;
	}

	@Override
	public HeartTypeTaking getTypeTaking()
	{
		switch ((TYPE_TAKING_BITS & flag) >> 3)
		{
			default ->
			{
				return HeartTypeTaking.NULL;
			}
			case 1 ->
			{
				return HeartTypeTaking.ENCHANT;
			}
			case 2 ->
			{
				return HeartTypeTaking.ABILITY;
			}
			case 3 ->
			{
				return HeartTypeTaking.PREENCHANT;
			}
		}
	}

	public HeartDataFlag setTypeTaking(HeartTypeTaking type)
	{
		switch (type)
		{
			case NULL -> flag = (flag & ~TYPE_TAKING_BITS);
			case ENCHANT -> flag = (flag & ~TYPE_TAKING_BITS) | 0b01000;
			case ABILITY -> flag = (flag & ~TYPE_TAKING_BITS) | 0b010000;
			case PREENCHANT -> flag = (flag & ~TYPE_TAKING_BITS) | 0b011000;
		}
		return this;
	}

	public HeartDataFlag setMaterialLevel(int lvl)
	{
		flag = (flag & ~MATERIAL_LEVEL_BITS) | Math.clamp(lvl - 1, 0, 7);
		return this;
	}

	public static HeartFlagOnlyCheckable getFromItemStack(ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.HEART_FLAG, EMPTY);
	}

	public static HeartFlagOnlyCheckable getFromItem(Item item)
	{
		return item.getComponents().getOrDefault(IWComponents.HEART_FLAG, EMPTY);
	}

	public static HeartDataFlag copyFromItemStack(ItemStack stack)
	{
		var ret = stack.getOrDefault(IWComponents.HEART_FLAG, null);
		if (ret == null) return new HeartDataFlag(0);
		else return new HeartDataFlag(ret.flag);
	}

	public static HeartDataFlag copyEmpty()
	{
		return new HeartDataFlag(EMPTY.flag);
	}
}
