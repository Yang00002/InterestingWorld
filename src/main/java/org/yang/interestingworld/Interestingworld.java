package org.yang.interestingworld;

import net.fabricmc.api.ModInitializer;
import org.yang.interestingworld.block.IWBlocks;
import org.yang.interestingworld.effect.IWEffects;
import org.yang.interestingworld.enchant.IWEnchantments;
import org.yang.interestingworld.entity.IWEntities;
import org.yang.interestingworld.item.IWItems;
import org.yang.interestingworld.loot.IWLoots;
import org.yang.interestingworld.network.IWNetwork;
import org.yang.interestingworld.particle_type.IWParticleTypes;
import org.yang.interestingworld.rune_ability.IWRuneAbilities;
import org.yang.interestingworld.rune_upgrade.IWRuneUpgrades;
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
		IWRuneAbilities.initialize();
		IWEnchantments.initialize();
		IWItems.initialize();
		IWRuneUpgrades.initialize();
		IWBlocks.initialize();
		IWScreenHandlers.initialize();
		IWResources.initialize();
		IWSounds.initialize();
		IWCommands.initialize();
		IWEntities.initialize();
		IWLoots.initialize();
		IWParticleTypes.initialize();
		IWItemGroups.initialize();
		IWNetwork.initialize();
	}
}
