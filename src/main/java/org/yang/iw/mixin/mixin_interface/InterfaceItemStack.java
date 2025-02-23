package org.yang.iw.mixin.mixin_interface;

import org.jetbrains.annotations.Nullable;
import org.yang.iw.boost.BoostContainer;
import org.yang.iw.component.AbilityComponent;
import org.yang.iw.component.BoostComponent;
import org.yang.iw.component.UpgradeComponent;

public interface InterfaceItemStack
{
	default BoostComponent interestingWorld$getBoosts()
	{
		return BoostComponent.DEFAULT;
	}

	default AbilityComponent interestingWorld$getAbility()
	{
		return AbilityComponent.DEFAULT;
	}

	default UpgradeComponent interestingWorld$getUpgrade()
	{
		return UpgradeComponent.DEFAULT;
	}

	default @Nullable BoostContainer interestingWorld$uniqueBoost()
	{
		return null;
	}
}
