package org.yang.interestingworld.rune_upgrade.sweeping;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.rune_upgrade.RuneUpgrade;
import org.yang.interestingworld.util.Base;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.toolflag.EnergyToolDataFlag;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sweeping4Upgrade extends RuneUpgrade
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

	@Override
	public void applyUpgrade(ItemStack toolStack)
	{
		super.applyUpgrade(toolStack);
		toolStack.set(IWComponents.TOOL_FLAG, EnergyToolDataFlag.copyFromItemStack(toolStack).setCanSweep());
		var at = toolStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		at = at.with(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO,
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
	public MutableText getTitleText()
	{
		return Text.translatable("sweeping_upgrade_title");
	}

	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		super.appendToolTip(tooltip);
		tooltip.add(Text.translatable("sweeping4_upgrade_detail").withColor(getColor()));
		appendIngredientToolTip(tooltip);
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
