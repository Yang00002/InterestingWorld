package org.yang.iw.util;

import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.yang.iw.component.IWComponents;

import java.util.ArrayList;

public class IWUtil
{
	public static CustomModelDataComponent getModelComponent(int id)
	{
		if (id <= 0) return CustomModelDataComponent.DEFAULT;
		ArrayList<Boolean> flagList = new ArrayList<>();
		while (id > 0)
		{
			if ((id & 1) > 0) flagList.add(true);
			else flagList.add(false);
			id >>= 1;
		}
		return new CustomModelDataComponent(null, flagList, null, null);
	}
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
				return ((haveBasicDamage && attributeEntryEqual(attribute, EntityAttributes.ATTACK_DAMAGE)) ||
						(haveBasicSpeed && attributeEntryEqual(attribute, EntityAttributes.ATTACK_SPEED))) &&
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
