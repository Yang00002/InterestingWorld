package org.yang.iw.util;

import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.yang.iw.effect.IWEffects;

public class IWDamageUtil
{
	public static float getArmoredDamage(LivingEntity entity, DamageSource source, float amount)
	{
		if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR))
		{
			var st = entity.getStatusEffect(IWEffects.HURTING);
			if (st != null)
			{
				float mul = 1.0f;
				var am = st.getAmplifier() + 1;
				if (am != 1) mul = (1 + am / 10.0f);
				amount = DamageUtil.getDamageLeft(entity, amount * mul, source, (float) entity.getArmor(),
						(float) entity.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS)) / mul;
			}
			else amount = DamageUtil.getDamageLeft(entity, amount, source, (float) entity.getArmor(),
					(float) entity.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS));
		}
		return amount;
	}
}
