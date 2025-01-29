package org.yang.interestingworld.rune_upgrade;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.interestingworld.util.style.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeavyUpgrade extends AbstractRuneUpgrade
{
	public static final float BASIC_DAMAGE_ADD = 0.12f;
	public static final float BASIC_SPEED_DOWN = 0.1f;
	private static final Map<Item, Integer> ingredientMap;

	static
	{
		Map<Item, Integer> map = new HashMap<>();
		map.put(Items.COBBLESTONE, 64);
		ingredientMap = Collections.unmodifiableMap(map);
	}

	@Override
	protected void applyUpgradeContent(ItemStack toolStack)
	{
		var at = toolStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
		for (var md : at.modifiers())
		{
			var modifier = md.modifier();
			var op = modifier.operation();
			if (op == EntityAttributeModifier.Operation.ADD_VALUE)
			{
				if (md.matches(EntityAttributes.GENERIC_ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_MODIFIER_ID))
				{
					builder.add(md.attribute(), new EntityAttributeModifier(modifier.id(),
							(modifier.value() + 1.0) * (1.0 + BASIC_DAMAGE_ADD) - 1.0, op), md.slot());
					continue;
				}
				else if (md.matches(EntityAttributes.GENERIC_ATTACK_SPEED, Item.BASE_ATTACK_SPEED_MODIFIER_ID))
				{
					builder.add(md.attribute(), new EntityAttributeModifier(modifier.id(),
							(4.0 + modifier.value()) * (1.0 - BASIC_SPEED_DOWN) - 4.0, op), md.slot());
					continue;
				}
			}
			builder.add(md.attribute(), modifier, md.slot());
		}
		toolStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
	}

	@Override
	public Map<Item, Integer> getIngredients()
	{
		return ingredientMap;
	}

	@Override
	public int level()
	{
		return 0;
	}

	@Override
	public MutableText getTitleText()
	{
		return Text.translatable("heavy_upgrade_title");
	}

	@Override
	public void appendExplanation(List<Text> tooltip)
	{
		tooltip.add(Text.translatable("heavy_upgrade_detail").withColor(getColor()));
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return true;
	}

	@Override
	public int getColor()
	{
		return Color.GRAY_RGB;
	}

	@Override
	public String getIdentifierString()
	{
		return "heavy";
	}
}
