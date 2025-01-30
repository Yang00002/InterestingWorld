package org.yang.iw;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_MODIFIER_ID;

public class IWUtil
{
	public static class Components
	{
		public static class MutableAttributeContainer
		{
			private boolean haveBasicDamage = false;
			private boolean haveBasicSpeed = false;

			public void findDamage()
			{
				haveBasicDamage = true;
			}

			public boolean hideAttribute(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier)
			{
				return ((haveBasicDamage && attributeEntryEqual(attribute, EntityAttributes.GENERIC_ATTACK_DAMAGE)) ||
						(haveBasicSpeed && attributeEntryEqual(attribute, EntityAttributes.GENERIC_ATTACK_SPEED))) &&
					   modifierAddByEnchantment(modifier);

			}

			public static boolean attributeEntryEqual(RegistryEntry<EntityAttribute> a1,
													  RegistryEntry<EntityAttribute> a2)
			{
				return a1.matches(a2);
			}

			public static boolean modifierAddByEnchantment(EntityAttributeModifier modifier)
			{
				return modifier.id().getNamespace().equals("iwenchant");
			}

			public void findSpeed()
			{
				haveBasicSpeed = true;
			}

			public void clear()
			{
				haveBasicDamage = false;
				haveBasicSpeed = false;
			}
		}


		public static double setbaseAttackDamageModifier(ItemStack stack, double damage2inModifier,
														 double defaultvalue)
		{
			double ret = defaultvalue;
			AttributeModifiersComponent am = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, null);
			AttributeModifiersComponent.Builder newbuilder = AttributeModifiersComponent.builder();
			if (am != null)
			{
				var list = am.modifiers();
				for (var i : list)
				{
					var mod = i.modifier();
					if (mod.idMatches(BASE_ATTACK_DAMAGE_MODIFIER_ID))
					{
						ret = mod.value();
						newbuilder.add(i.attribute(),
								new EntityAttributeModifier(mod.id(), damage2inModifier, mod.operation()), i.slot());
					}
					else
					{
						newbuilder.add(i.attribute(), mod, i.slot());
					}
				}
			}
			else
			{
				newbuilder.add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
						new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, damage2inModifier,
								EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
			}
			stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, newbuilder.build());
			return ret;
		}

		public static void popEnchantments(ItemStack stack)
		{
			ItemEnchantmentsComponent before = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS,
					ItemEnchantmentsComponent.DEFAULT);
			if (before.isEmpty())
			{
				stack.remove(DataComponentTypes.ENCHANTMENTS);
				stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
				return;
			}
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(before);
			ItemEnchantmentsComponent after = builder.build();
			stack.set(DataComponentTypes.STORED_ENCHANTMENTS, after);
			stack.remove(DataComponentTypes.ENCHANTMENTS);
		}

		public static void pushEnchantments(ItemStack stack)
		{
			ItemEnchantmentsComponent before = stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS,
					ItemEnchantmentsComponent.DEFAULT);
			if (before.isEmpty())
			{
				stack.remove(DataComponentTypes.ENCHANTMENTS);
				stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
				return;
			}
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(before);
			ItemEnchantmentsComponent after = builder.build();
			stack.set(DataComponentTypes.ENCHANTMENTS, after);
			stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
		}

		public static boolean hasEnchantment(ItemStack stack)
		{
			var ec = stack.get(DataComponentTypes.ENCHANTMENTS);
			if (ec == null) return false;
			return !ec.isEmpty();
		}

		public static float maxEnergy(ItemStack stack)
		{
			return stack.getOrDefault(IWComponents.MAX_ENERGY, 0.0f);
		}

		public static float currentEnergy(ItemStack stack)
		{
			return stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0.0f);
		}
	}
}
