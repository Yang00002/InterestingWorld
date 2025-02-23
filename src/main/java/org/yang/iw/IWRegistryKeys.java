package org.yang.iw;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.api.register.RegistryCollector;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.upgrade.AbstractUpgrade;

@RegistryCollector
public class IWRegistryKeys
{
	public static final RegistryKey<Registry<AbstractAbility>> ABILITY = new RegistryKeys().interestingWorld$ability();
	public static final RegistryKey<Registry<AbstractBoost>> BOOST = new RegistryKeys().interestingWorld$boost();
	public static final RegistryKey<Registry<AbstractUpgrade>> UPGRADE = new RegistryKeys().interestingWorld$upgrade();
}
