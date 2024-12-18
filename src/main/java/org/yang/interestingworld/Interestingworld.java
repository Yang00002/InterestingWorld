package org.yang.interestingworld;

import net.fabricmc.api.ModInitializer;
import org.yang.interestingworld.rune.IWRuneAbilitys;
import org.yang.interestingworld.rune.IWRuneUpgrades;
import org.yang.interestingworld.util.Server;


public class Interestingworld implements ModInitializer
{

	@Override
	public void onInitialize()
	{
		Server.initialize();
		IWDamageTypes.initialize();
		IWEffects.initialize();
		IWComponents.initialize();
		IWRuneAbilitys.initialize();
		IWEnchantments.initialize();
		IWItems.initialize();
		IWRuneUpgrades.initialize();
		IWBlocks.initialize();
		IWItemGroups.initialize();
		IWScreenHandlers.initialize();
		IWResources.initialize();
		IWSounds.initialize();
		IWCommands.initialize();
		IWEntities.initialize();
		IWLoots.initialize();
	}


}
