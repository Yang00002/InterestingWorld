package org.yang.iw.boost;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.yang.iw.IWRegistries;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.Base;

import java.util.function.Function;

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
	public static final AbstractBoost UNBREAKING = register(UnbreakingBoost::new, "unbreaking", "耐久");
	public static final AbstractBoost SWEEPING_EDGE = register(SweepingEdgeBoost::new, "sweeping_edge", "横扫之刃");
	public static final AbstractBoost MENDING = register(MendingBoost::new, "mending", "经验修补");
	public static final AbstractBoost LOOTING = register(LootingBoost::new, "looting", "抢夺");
	public static final AbstractBoost SMITE = register(SmiteBoost::new, "smite", "亡灵杀手");
	public static final AbstractBoost BANE_OF_ARTHROPODS = register(BaneOfArthropodsBoost::new, "bane_of_arthropods",
			"节肢杀手");
	public static final AbstractBoost BIG_NOSE_LOVER = register(BigNoseLoverBoost::new, "big_nose_lover", "村民之友");
	public static final AbstractBoost KNOCKBACK = register(KnockbackBoost::new, "knockback", "击退");
	public static final AbstractBoost ENERGY_SAVING = register(EnergySavingBoost::new, "energy_saving", "蓄能");
	public static final AbstractBoost FAST_TRANSFER = register(FastTransferBoost::new, "fast_transfer", "快速传输");

	public static AbstractBoost register(Function<Identifier, AbstractBoost> boostFunction, String id, String name)
	{
		var boost = boostFunction.apply(Identifier.of(Base.MOD_ID, id));
		if (!checkValid(boost)) throw new RuntimeException("boost " + id + " is invalid");
		TranslationPool.addString(boost.translationKey(), name);
		Registry.register(IWRegistries.BOOST, RegistryKey.of(IWRegistryKeys.BOOST, boost.identifier), boost);
		return boost;
	}

	public static boolean checkValid(AbstractBoost boost)
	{
		int maxAllow = boost.maxAllowLevel();
		int maxRand = boost.maxRandomLevel();
		int maxTab = boost.maxTableLevel();
		if (maxAllow > 255) return false;
		if (maxAllow < maxRand) return false;
		if (maxRand < maxTab) return false;
		if (maxTab < 1) return false;
		if (boost.xpCostOfLevel((short) 0) != 0) return false;
		int pre = 0;
		for (int i = 1; i <= maxAllow; i++)
		{
			int cur = boost.xpCostOfLevel((short) i);
			if (cur <= pre) return false;
			pre = cur;
		}
		return true;
	}

	static
	{
		LoadTime.setLoaded(IWBoosts.class);
	}


	public static void initialize()
	{

	}
}
