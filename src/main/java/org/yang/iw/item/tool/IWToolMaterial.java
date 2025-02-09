package org.yang.iw.item.tool;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

/**
 * 这些 Modifier 最终将玩家的基础数据加入 base 进行计算, 得到最终的 add 数值
 * 例如 +1, *2 的攻击力更改将得到 (1+1)*2=4, 最终纸面攻击力是 4, 而应用在工具上是攻击力 +3
 */
public record IWToolMaterial(MinimizedAttributeModifier durabilityModifier,
							 MinimizedAttributeModifier attackDamageModifier,
							 MinimizedAttributeModifier attackSpeedModifier,
							 MinimizedAttributeModifier enchantmentValueModifier, TagKey<Item> repairItems)
{
	public static final IWToolMaterial WOOD = new IWToolMaterial(new MinimizedAttributeModifier(64, 0, 0),
			new MinimizedAttributeModifier(0, 0, 0), new MinimizedAttributeModifier(0, 0, 0),
			new MinimizedAttributeModifier(15, 0, 0), ItemTags.WOODEN_TOOL_MATERIALS);
	public static final IWToolMaterial STONE = new IWToolMaterial(new MinimizedAttributeModifier(128, 0, 0),
			new MinimizedAttributeModifier(1, 0, 0), new MinimizedAttributeModifier(0, 0, 0),
			new MinimizedAttributeModifier(5, 0, 0), ItemTags.STONE_TOOL_MATERIALS);
	public static final IWToolMaterial IRON = new IWToolMaterial(new MinimizedAttributeModifier(256, 0, 0),
			new MinimizedAttributeModifier(2, 0, 0), new MinimizedAttributeModifier(0, 0, 0),
			new MinimizedAttributeModifier(14, 0, 0), ItemTags.IRON_TOOL_MATERIALS);
	public static final IWToolMaterial GOLD = new IWToolMaterial(new MinimizedAttributeModifier(256, 0, -7f / 8),
			new MinimizedAttributeModifier(2, 0, -1f / 3), new MinimizedAttributeModifier(0, 0, 0),
			new MinimizedAttributeModifier(22, 0, 0), ItemTags.GOLD_TOOL_MATERIALS);
	public static final IWToolMaterial DIAMOND = new IWToolMaterial(new MinimizedAttributeModifier(512, 0, 1),
			new MinimizedAttributeModifier(3, 0, 0), new MinimizedAttributeModifier(0, 0, 0),
			new MinimizedAttributeModifier(10, 0, 0), ItemTags.DIAMOND_TOOL_MATERIALS);
	public static final IWToolMaterial NETHERITE = new IWToolMaterial(new MinimizedAttributeModifier(1024, 0, 1),
			new MinimizedAttributeModifier(4, 0, 0), new MinimizedAttributeModifier(0, 0, 0),
			new MinimizedAttributeModifier(15, 0, 0), ItemTags.NETHERITE_TOOL_MATERIALS);
	public static final IWToolMaterial BLAZE = new IWToolMaterial(new MinimizedAttributeModifier(256, 0, -0.5f),
			new MinimizedAttributeModifier(2, 0, -0.4f), new MinimizedAttributeModifier(0, 0, 0),
			new MinimizedAttributeModifier(10, 0, 0), ItemTags.STONE_TOOL_MATERIALS);

	public static final ToolTypeHandler ROD = new ToolTypeHandler().setAttackDamageModifier(2, 0, 0);
	public static final ToolTypeHandler SWORD = new ToolTypeHandler().setAttackSpeedModifier(-2.4f, 0, 0)
			.setAttackDamageModifier(3, 0, 0);


	public static class ToolTypeHandler
	{
		MinimizedAttributeModifier durabilityModifier = MinimizedAttributeModifier.DEFAULT;
		MinimizedAttributeModifier attackSpeedModifier = MinimizedAttributeModifier.DEFAULT;
		MinimizedAttributeModifier attackDamageModifier = MinimizedAttributeModifier.DEFAULT;
		MinimizedAttributeModifier enchantmentValueModifier = MinimizedAttributeModifier.DEFAULT;

		public ToolTypeHandler setDurabilityModifier(float a, float s, float m)
		{
			durabilityModifier = new MinimizedAttributeModifier(a, s, m);
			return this;
		}

		public ToolTypeHandler setAttackSpeedModifier(float a, float s, float m)
		{
			attackSpeedModifier = new MinimizedAttributeModifier(a, s, m);
			return this;
		}

		public ToolTypeHandler setAttackDamageModifier(float a, float s, float m)
		{
			attackDamageModifier = new MinimizedAttributeModifier(a, s, m);
			return this;
		}
	}

	public Item.Settings applySettings(Item.Settings settings, ToolTypeHandler handler)
	{
		var settings1 = settings.maxDamage(Math.max(1,
						(int) MinimizedAttributeModifier.apply(List.of(durabilityModifier, handler.durabilityModifier)
								, 0)))
				.repairable(this.repairItems).attributeModifiers(AttributeModifiersComponent.builder()
						.add(EntityAttributes.ATTACK_DAMAGE,
								new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, Math.max(1,
										MinimizedAttributeModifier.apply(
												List.of(attackDamageModifier, handler.attackDamageModifier), 1)) - 1,
										EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
						.add(EntityAttributes.ATTACK_SPEED,
								new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, Math.max(1,
										MinimizedAttributeModifier.apply(
												List.of(attackSpeedModifier, handler.attackSpeedModifier), 1)) - 4,
										EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
						.build());
		int enchantmentValue = (int) MinimizedAttributeModifier.apply(
				List.of(enchantmentValueModifier, handler.enchantmentValueModifier), 0);
		if (enchantmentValue > 0) settings1.enchantable(enchantmentValue);
		return settings1;
	}
}
