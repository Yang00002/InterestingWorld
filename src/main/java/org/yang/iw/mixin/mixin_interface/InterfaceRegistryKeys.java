package org.yang.iw.mixin.mixin_interface;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.upgrade.AbstractUpgrade;

public interface InterfaceRegistryKeys
{
	default RegistryKey<Registry<AbstractAbility>> interestingWorld$ability()
	{
		return null;
	}
	default RegistryKey<Registry<AbstractBoost>> interestingWorld$boost()
	{
		return null;
	}
	default RegistryKey<Registry<AbstractUpgrade>> interestingWorld$upgrade()
	{
		return null;
	}

}
