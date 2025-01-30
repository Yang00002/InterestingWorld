package org.yang.iw.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.Base;

public class IWEffects
{
	public static final RegistryEntry<StatusEffect> BLOOD = registerEffect("blood", "流血", new BloodEffect());
	public static final RegistryEntry<StatusEffect> COOLDOWN = registerEffect("cooldown", "受击", new CooldownEffect());
	public static final RegistryEntry<StatusEffect> HURTING = registerEffect("hurting", "易伤", new HurtingEffect());
	public static final RegistryEntry<StatusEffect> INFINITECURSE = registerEffect("infinitecurse", "无限诅咒",
			new InfiniteCurseEffect());

	private static RegistryEntry<StatusEffect> registerEffect(String id, String translate, StatusEffect effect)
	{
		var ret = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Base.MOD_ID, id), effect);
		TranslationPool.addStatusEffect(effect, translate);
		return ret;
	}


	public static void initialize()
	{
	}
}