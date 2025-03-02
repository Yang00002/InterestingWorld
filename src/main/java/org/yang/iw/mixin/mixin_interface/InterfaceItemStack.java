package org.yang.iw.mixin.mixin_interface;

import org.jetbrains.annotations.Nullable;
import org.yang.iw.boost.BoostContainer;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.component.AbilityComponent;
import org.yang.iw.component.BoostComponent;
import org.yang.iw.component.UpgradeComponent;

import java.util.function.Function;

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

	default @Nullable BoostFunctionMap interestingWorld$uniqueBoost()
	{
		return null;
	}
}
