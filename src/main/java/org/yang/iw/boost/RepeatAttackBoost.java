package org.yang.iw.boost;

import com.google.common.collect.HashMultimap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.yang.iw.IWEntityAttributes;
import org.yang.iw.boost.function.AttributeModifierFunction;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.util.constants.AttributeModifierIds;

public class RepeatAttackBoost extends AbstractBoost
{
	@Override
	public BoostFunctionMap getFunctions(int level)
	{
		return BoostFunctionMap.builder().add(AttributeModifierSlot.MAINHAND, new AttributeModifierFunction()
		{
			@Override
			protected HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(StringIdentifiable slot)
			{
				HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifierMultiMap =
						HashMultimap.create();
				modifierMultiMap.put(IWEntityAttributes.ATTACK_DURATION_NEGLECT,
						new EntityAttributeModifier(AttributeModifierIds.of(identifier, slot), level,
								EntityAttributeModifier.Operation.ADD_VALUE));
				return modifierMultiMap;
			}
		}).build();
	}

	RepeatAttackBoost(Identifier identifier)
	{
		super(identifier);
	}

	public short maxTableLevel()
	{
		return 4;
	}

	public short maxRandomLevel()
	{
		return 9;
	}

	@Override
	public int xpCostOfLevel(short level)
	{
		return level > 0 ? 120 + 80 * level : 0;
	}

	@Override
	public int xpCostBetweenLevels(short low, short high)
	{
		return low > 0 ? 80 * (high - low) : (high > 0 ? 120 + 80 * high : 0);
	}

	@Override
	public short costAchieveLevel(short from, short to, int costAll)
	{
		return (short) Math.clamp((costAll - 120) / 80, from, to);
	}
}
