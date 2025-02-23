package org.yang.iw.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class CommonEffect extends StatusEffect
{
	public static Identifier attributeModifierID(Identifier identifier)
	{
		return Identifier.of(identifier.getNamespace(), identifier.getPath() + ".effect.modifier");
	}

	public static Identifier attributeModifierID(Identifier identifier, int idx)
	{
		return Identifier.of(identifier.getNamespace(), identifier.getPath() + ".effect.modifier." + idx);
	}

	public CommonEffect(StatusEffectCategory category, int color)
	{
		super(category, color);
	}


}
