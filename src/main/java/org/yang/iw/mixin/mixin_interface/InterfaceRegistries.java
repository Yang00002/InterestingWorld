package org.yang.iw.mixin.mixin_interface;

import net.minecraft.registry.Registry;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.upgrade.AbstractUpgrade;

public interface InterfaceRegistries
{
	default Registry<AbstractAbility> interestingWorld$ability()
	{
		return null;
	}

	default Registry<AbstractBoost> interestingWorld$boost()
	{
		return null;
	}

	default Registry<AbstractUpgrade> interestingWorld$upgrade()
	{
		return null;
	}

}
