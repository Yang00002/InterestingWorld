package org.yang.iw.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.yang.iw.IWEntityAttributes;
import org.yang.iw.api.register.DataGenSupplier;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.Base;

import java.util.function.Function;

import static org.yang.iw.util.Base.iwlogger;

@DataGenSupplier
@IndependentRegister
public class IWEffects
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
	}

	public static final RegistryEntry<StatusEffect> BLOOD = registerEffect("blood", "流血", new BloodEffect());
	public static final RegistryEntry<StatusEffect> COOLDOWN = registerEffect("cooldown", "受击", new CooldownEffect());
	public static final RegistryEntry<StatusEffect> HURTING = registerCommonEffect("hurting", "易伤",
			identifier -> new CommonEffect(StatusEffectCategory.HARMFUL, 0X000000).addAttributeModifier(
					IWEntityAttributes.HURT_DAMAGE_MULTIPLIER, identifier, 0.1,
					EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
	public static final RegistryEntry<StatusEffect> INFINITECURSE = registerEffect("infinitecurse", "无限诅咒",
			new InfiniteCurseEffect());
	public static final RegistryEntry<StatusEffect> BURNING = registerEffect("burning", "易燃", new BurningEffect());

	private static RegistryEntry<StatusEffect> registerEffect(String id, String translate, StatusEffect effect)
	{
		var ret = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Base.MOD_ID, id), effect);
		TranslationPool.addStatusEffect(effect, translate);
		return ret;
	}

	private static RegistryEntry<StatusEffect> registerCommonEffect(String id, String translate, Function<Identifier,
			StatusEffect> effect)
	{
		var identifier = Identifier.of(Base.MOD_ID, id);
		var statusEffect = effect.apply(identifier);
		var ret = Registry.registerReference(Registries.STATUS_EFFECT, identifier, statusEffect);
		TranslationPool.addStatusEffect(statusEffect, translate);
		return ret;
	}


	public static void initialize()
	{
	}
}