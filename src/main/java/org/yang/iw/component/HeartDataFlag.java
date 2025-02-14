package org.yang.iw.component;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public record HeartDataFlag(int value)
{
	public enum HeartTypeTaking
	{
		NULL, ENCHANT, ABILITY, PREENCHANT
	}

	public static class Builder
	{
		int value;

		public HeartDataFlag build()
		{
			return new HeartDataFlag(value);
		}

		public void dump(ItemStack stack)
		{
			stack.set(IWComponents.HEART_FLAG, build());
		}

		public void dump(Item.Settings settings)
		{
			settings.component(IWComponents.HEART_FLAG, build());
		}

		public Builder setTakingLevel(int lvl)
		{
			value = (value & ~TAKING_LEVEL_BITS) | ((Math.clamp(lvl, 0, 10)) << 6);
			return this;
		}

		public Builder setTypeTaking(HeartTypeTaking type)
		{
			switch (type)
			{
				case NULL -> value = (value & ~TYPE_TAKING_BITS);
				case ENCHANT -> value = (value & ~TYPE_TAKING_BITS) | 0b010000;
				case ABILITY -> value = (value & ~TYPE_TAKING_BITS) | 0b0100000;
				case PREENCHANT -> value = (value & ~TYPE_TAKING_BITS) | 0b0110000;
			}
			return this;
		}

		public Builder setMaterialLevel(int lvl)
		{
			value = (value & ~MATERIAL_LEVEL_BITS) | Math.clamp(lvl, 0, 10);
			return this;
		}
	}

	public static final HeartDataFlag EMPTY = new HeartDataFlag(0);

	public static Codec<HeartDataFlag> CODEC = Codec.INT.xmap(HeartDataFlag::new, HeartDataFlag::value);
	/**
	 * 材料等级 bit 4 位, 在 0 - 10 之间
	 */
	private static final int MATERIAL_LEVEL_BITS = 0b01111;

	private static final int TYPE_TAKING_BITS = 0b0110000;
	private static final int TAKING_LEVEL_BITS = 0b01111000000;

	public int getMaterialLevel()
	{
		return MATERIAL_LEVEL_BITS & value;
	}

	public int getTakingLevel()
	{
		return (value & TAKING_LEVEL_BITS) >> 6;
	}


	public HeartTypeTaking getTypeTaking()
	{
		switch ((TYPE_TAKING_BITS & value) >> 4)
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

	public static HeartDataFlag fromItemStack(ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.HEART_FLAG, EMPTY);
	}

	public static HeartDataFlag fromItem(Item stack)
	{
		return stack.getComponents().getOrDefault(IWComponents.HEART_FLAG, EMPTY);
	}

	public HeartDataFlag.Builder getBuilder()
	{
		var ret = new HeartDataFlag.Builder();
		ret.value = value;
		return ret;
	}

	public static HeartDataFlag.Builder builder()
	{
		return EMPTY.getBuilder();
	}

	public static HeartDataFlag.Builder builder(ItemStack stack)
	{
		return fromItemStack(stack).getBuilder();
	}

}
