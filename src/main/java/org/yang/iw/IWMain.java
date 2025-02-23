package org.yang.iw;

import net.fabricmc.api.ModInitializer;
import org.yang.iw.ability.IWAbilities;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.block.IWBlocks;
import org.yang.iw.boost.IWBoosts;
import org.yang.iw.component.IWComponents;
import org.yang.iw.effect.IWEffects;
import org.yang.iw.entity.IWEntities;
import org.yang.iw.item.IWItems;
import org.yang.iw.network.IWNetwork;
import org.yang.iw.particle_type.IWParticleTypes;
import org.yang.iw.upgrade.IWUpgrades;
import org.yang.iw.util.Server;


public class IWMain implements ModInitializer
{
	private void initialize()
	{
		Server.initialize();
		IWEntityAttributes.initialize();
		IWBoosts.initialize();
		IWComponents.initialize();
		IWItemGroups.initialize();
		IWEffects.initialize();
		IWScreenHandlers.initialize();
		IWResources.initialize();
		IWSounds.initialize();
		IWCommands.initialize();
		IWEntities.initialize();
		IWParticleTypes.initialize();
		IWNetwork.initialize();
	}

	private void initializeSequenced()
	{
		IWUpgrades.initialize(); // need itemGroups
		IWItems.initialize(); // need itemGroups
		IWBlocks.initialize(); // need itemGroups

		IWAbilities.initialize(); // need items
	}

	private void initializeLoadOnce()
	{
		IWDamageTypes.initialize();
	}

	private void afterInitialize()
	{
		IWItemGroups.afterInitialize();
	}

	@Override
	public void onInitialize()
	{
		LoadTime.startInitialize();
		initialize();
		initializeSequenced();
		initializeLoadOnce();
		LoadTime.finishInitialize();
		afterInitialize();
	}
}
