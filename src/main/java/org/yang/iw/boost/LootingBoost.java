package org.yang.iw.boost;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.function.EquipmentDropChanceFunction;
import org.yang.iw.boost.function.PretendEnchantInLootTableFunction;

import static org.yang.iw.util.Base.iwlogger;

public class LootingBoost extends AbstractBoost
{
	@Override
	public BoostFunctionMap getFunctions(int level)
	{
		return BoostFunctionMap.builder().add(AttributeModifierSlot.MAINHAND, new EquipmentDropChanceFunction()
		{
			@Override
			public void getEquipmentDropChance(ServerWorld world, LivingEntity attacker, DamageSource damageSource,
											   MutableFloat baseEquipmentDropChance)
			{
				baseEquipmentDropChance.add(level * 0.01);
				iwlogger.info(baseEquipmentDropChance.getValue());
			}
		}).add(AttributeModifierSlot.MAINHAND, new PretendEnchantInLootTableFunction()
		{
			@Override
			public RegistryKey<Enchantment> type()
			{
				return Enchantments.LOOTING;
			}

			@Override
			public float level()
			{
				return level;
			}
		}).build();
	}

	LootingBoost(Identifier identifier)
	{
		super(identifier);
	}

	@Override
	public int xpCostBetweenLevels(short low, short high)
	{
		return 250 * (high - low);
	}

	public short maxTableLevel()
	{
		return 3;
	}

	public short maxRandomLevel()
	{
		return 6;
	}

	@Override
	public int xpCostOfLevel(short level)
	{
		return 250 * level;
	}

	@Override
	public short costAchieveLevel(short from, short to, int costAll)
	{
		return (short) Math.clamp(costAll / 250, from, to);
	}
}
