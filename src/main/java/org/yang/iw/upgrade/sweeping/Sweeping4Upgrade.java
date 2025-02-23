package org.yang.iw.upgrade.sweeping;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.upgrade.AbstractUpgrade;
import org.yang.iw.util.Base;
import org.yang.iw.util.style.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Sweeping4Upgrade extends AbstractUpgrade
{

	public static final float SWEEP_RATIO = 1.0f;

	private static final Map<Item, Integer> ingredientMap;

	static
	{
		Map<Item, Integer> map = new HashMap<>();
		map.put(Items.STICK, 32);
		map.put(Items.IRON_INGOT, 16);
		map.put(Items.AMETHYST_SHARD, 4);
		ingredientMap = Collections.unmodifiableMap(map);
	}

	public Sweeping4Upgrade(String identifier)
	{
		super(identifier);
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return !EnergyToolDataFlag.fromItemStack(stack).canSweep();
	}


	@Override
	protected void applyUpgradeContent(ItemStack toolStack)
	{
		EnergyToolDataFlag.builder(toolStack).setCanSweep().dump(toolStack);
		var at = toolStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		at = at.with(EntityAttributes.SWEEPING_DAMAGE_RATIO,
				new EntityAttributeModifier(Identifier.of(Base.MOD_ID, "ecg"), SWEEP_RATIO,
						EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
		toolStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, at);
	}

	@Override
	public Map<Item, Integer> getIngredients()
	{
		return ingredientMap;
	}

	@Override
	public int level()
	{
		return 5;
	}

	@Override
	public int getColor()
	{
		return Color.DARK_PURPLE_RGB;
	}


}
