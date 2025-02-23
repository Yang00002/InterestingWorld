package org.yang.iw;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.api.register.RegistryCollector;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.upgrade.AbstractUpgrade;

@RegistryCollector
public class IWRegistries
{
	public static final Registry<AbstractAbility> ABILITY = new Registries().interestingWorld$ability();
	public static final Registry<AbstractBoost> BOOST = new Registries().interestingWorld$boost();
	public static final Registry<AbstractUpgrade> UPGRADE = new Registries().interestingWorld$upgrade();
}
