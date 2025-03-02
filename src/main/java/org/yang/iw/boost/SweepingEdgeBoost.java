package org.yang.iw.boost;

import com.google.common.collect.HashMultimap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.yang.iw.boost.function.AttributeModifierFunction;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.util.constants.AttributeModifierIds;

public class SweepingEdgeBoost extends AbstractBoost
{
	SweepingEdgeBoost(Identifier identifier)
	{
		super(identifier);
	}

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
				modifierMultiMap.put(EntityAttributes.SWEEPING_DAMAGE_RATIO,
						new EntityAttributeModifier(AttributeModifierIds.of(identifier, slot), level * 0.1,
								EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
				return modifierMultiMap;
			}
		}).build();
	}


	@Override
	public int xpCostOfLevel(short level)
	{
		return level * 150;
	}

	@Override
	public int xpCostBetweenLevels(short low, short high)
	{
		return (high - low) * 150;
	}

	@Override
	public short costAchieveLevel(short from, short to, int costAll)
	{
		return (short) Math.clamp((costAll / 150), from, to);
	}

	public short maxTableLevel()
	{
		return 5;
	}

	public short maxRandomLevel()
	{
		return 9;
	}

	public short maxAllowLevel()
	{
		return 10;
	}
}
