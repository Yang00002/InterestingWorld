package org.yang.interestingworld;

import net.fabricmc.api.ModInitializer;
import org.yang.interestingworld.rune.IWRuneAbilitys;


public class Interestingworld implements ModInitializer
{

	@Override
	public void onInitialize()
	{
		IWUtil.Server.initialize();
		IWDamageTypes.initialize();
		IWEffects.initialize();
		IWComponents.initialize();
		IWRuneAbilitys.initialize();
		IWEnchantments.initialize();
		IWItems.initialize();
		IWBlocks.initialize();
		IWItemGroups.initialize();
		IWScreenHandlers.initialize();
		IWResources.initialize();
		IWSounds.initialize();
		IWCommands.initialize();
		IWEntities.initialize();
	}


}
