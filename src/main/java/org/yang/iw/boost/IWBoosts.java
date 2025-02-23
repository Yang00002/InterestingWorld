package org.yang.iw.boost;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.yang.iw.IWRegistries;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.api.register.DataGenSupplier;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.Base;

import java.util.function.Function;

@DataGenSupplier
@IndependentRegister
public class IWBoosts
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
	}

	public static final AbstractBoost REPEAT_ATTACK = register(RepeatAttackBoost::new, "repeat_attack", "连击");
	public static final AbstractBoost SHARPNESS = register(SharpnessBoost::new, "sharpness", "锋利");
	public static final AbstractBoost FIRE_ASPECT = register(FireAspectBoost::new, "fire_aspect", "火焰附加");
	public static final AbstractBoost FAST_ATTACK = register(FastAttackBoost::new, "fast_attack", "迅捷打击");

	public static AbstractBoost register(Function<Identifier, AbstractBoost> boostFunction, String id, String name)
	{
		var boost = boostFunction.apply(Identifier.of(Base.MOD_ID, id));
		TranslationPool.addString(boost.translationKey(), name);
		Registry.register(IWRegistries.BOOST, RegistryKey.of(IWRegistryKeys.BOOST, boost.identifier), boost);
		return boost;
	}


	public static void initialize()
	{

	}
}
