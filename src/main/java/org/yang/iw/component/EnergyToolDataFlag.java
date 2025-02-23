package org.yang.iw.component;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.yang.iw.util.style.Color;

public record EnergyToolDataFlag(int value)
{

	private static final int LEVEL_FLAG = 0b01111;
	private static final int LEVEL_UPGRADE_FLAG = 0b011110000;
	private static final int REAL_ENCHANT_FLAG = 0b0100000000;
	private static final int DEFAULT_ENCHANT_FLAG = 0b01000000000;
	private static final int SWEEP_FLAG = 0b010000000000;

	public static class Builder
	{
		int value;

		public Builder setCanSweep()
		{
			value |= SWEEP_FLAG;
			return this;
		}

		public Builder setUpgradeLevel(int level)
		{
			value = (value & ~LEVEL_UPGRADE_FLAG) | ((level + 1) << 4);
			return this;
		}

		public Builder removeCanSweep()
		{
			value &= ~SWEEP_FLAG;
			return this;
		}

		public Builder setHaveRealEnchantment()
		{
			value |= REAL_ENCHANT_FLAG;
			return this;
		}

		public Builder removeHaveRealEnchantment()
		{
			value &= ~REAL_ENCHANT_FLAG;
			return this;
		}

		public Builder setHaveDefaultEnchantment()
		{
			value |= DEFAULT_ENCHANT_FLAG;
			return this;
		}

		public Builder removeHaveDefaultEnchantment()
		{
			value &= ~DEFAULT_ENCHANT_FLAG;
			return this;
		}

		public Builder setLevel(int level)
		{
			value = (value & ~LEVEL_FLAG) | level;
			return this;
		}

		// 从 ItemStack 获取 ability_level 和 boost_level
		public Builder updateLevelFromItemStack(ItemStack stack)
		{
			var ab = stack.interestingWorld$getAbility().ability();
			int ability_level = (ab.isEmpty() ? -1 : ab.level());
			int upgrade_level = ((value & LEVEL_UPGRADE_FLAG) >> 4) - 1;
			var boosts = stack.interestingWorld$getBoosts();
			int boost_level = (boosts.isEmpty() || boosts.onlyDefault()) ? boosts.level() : -1;
			int maxLevel = Math.max(ability_level, Math.max(upgrade_level, boost_level));
			if (maxLevel == -1) value &= ~LEVEL_FLAG;
			else
			{
				int count = 0;
				if (ability_level >= maxLevel) count++;
				if (upgrade_level >= maxLevel) count++;
				if (boost_level >= maxLevel) count++;
				if (count > 1 && maxLevel < 10) maxLevel++;
				value = (value & ~LEVEL_FLAG) | maxLevel;
			}
			return this;
		}

		public EnergyToolDataFlag build()
		{
			return new EnergyToolDataFlag(value);
		}

		public void dump(ItemStack stack)
		{
			stack.set(IWComponents.TOOL_FLAG, build());
		}

		public void dump(Item.Settings settings)
		{
			settings.component(IWComponents.TOOL_FLAG, build());
		}
	}

	public static final EnergyToolDataFlag EMPTY = new EnergyToolDataFlag(0);

	public static Codec<EnergyToolDataFlag> CODEC = Codec.INT.xmap(EnergyToolDataFlag::new, EnergyToolDataFlag::value);

	public static EnergyToolDataFlag fromItemStack(ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.TOOL_FLAG, EMPTY);
	}

	public Builder getBuilder()
	{
		var ret = new Builder();
		ret.value = value;
		return ret;
	}

	public static Builder builder()
	{
		return EMPTY.getBuilder();
	}

	public static Builder builder(ItemStack stack)
	{
		return fromItemStack(stack).getBuilder();
	}

	public boolean canSweep()
	{
		return (value & SWEEP_FLAG) > 0;
	}


	public boolean haveRealEnchantment()
	{
		return (value & REAL_ENCHANT_FLAG) > 0;
	}


	public boolean haveDefaultEnchantment()
	{
		return (value & DEFAULT_ENCHANT_FLAG) > 0;
	}

	public boolean onlyHaveDefaultEnchantment()
	{
		return ((value & DEFAULT_ENCHANT_FLAG) > 0) && ((value & REAL_ENCHANT_FLAG) < 1);
	}

	public boolean haveUpgrade()
	{
		return ((value & LEVEL_UPGRADE_FLAG) >> 4) > 0;
	}


	public int level()
	{
		return value & LEVEL_FLAG;
	}

	public int levelColor()
	{
		return Color.getLevelColor(value & LEVEL_FLAG);
	}


}
