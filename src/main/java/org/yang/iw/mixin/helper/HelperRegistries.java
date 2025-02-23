package org.yang.iw.mixin.helper;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.upgrade.AbstractUpgrade;
import org.yang.iw.util.Base;

public class HelperRegistries
{
	public static AbstractAbility registerDefaultAbility(Registry<AbstractAbility> registry)
	{
		var ret = AbstractAbility.getDefault();
		return Registry.register(registry, RegistryKey.of(IWRegistryKeys.ABILITY, ret.identifier()), ret);
	}

	public static AbstractBoost registerDefaultBoost(Registry<AbstractBoost> registry)
	{
		var ret = AbstractBoost.getDefault();
		return Registry.register(registry, RegistryKey.of(IWRegistryKeys.BOOST, ret.identifier), ret);
	}

	public static AbstractUpgrade registerDefaultUpgrade(Registry<AbstractUpgrade> registry)
	{
		var ret = AbstractUpgrade.getDefault();
		return Registry.register(registry,
				RegistryKey.of(IWRegistryKeys.UPGRADE, Identifier.of(Base.MOD_ID, ret.identifier())), ret);
	}
}
