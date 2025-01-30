package org.yang.iw;

import net.fabricmc.api.ModInitializer;
import org.yang.iw.block.IWBlocks;
import org.yang.iw.effect.IWEffects;
import org.yang.iw.enchant.IWEnchantments;
import org.yang.iw.entity.IWEntities;
import org.yang.iw.item.IWItemTags;
import org.yang.iw.item.IWItems;
import org.yang.iw.loot.IWLoots;
import org.yang.iw.network.IWNetwork;
import org.yang.iw.particle_type.IWParticleTypes;
import org.yang.iw.rune_ability.IWRuneAbilities;
import org.yang.iw.rune_upgrade.IWRuneUpgrades;
import org.yang.iw.util.Server;


public class IWMain implements ModInitializer
{

	@Override
	public void onInitialize()
	{
		Server.initialize();
		IWItemTags.initialize();
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
