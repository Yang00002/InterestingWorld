package org.yang.iw;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.yang.iw.ability.IWAbilities;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.block.IWBlocks;
import org.yang.iw.boost.IWBoosts;
import org.yang.iw.boost.pool.RandomBoostPool;
import org.yang.iw.boost.pool.TableBoostPool;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.IWDataGen;
import org.yang.iw.effect.IWEffects;
import org.yang.iw.entity.IWEntities;
import org.yang.iw.item.IWItems;
import org.yang.iw.network.IWNetwork;
import org.yang.iw.particle_type.IWParticleTypes;
import org.yang.iw.persistentdata.IWPersistentData;
import org.yang.iw.resource.IWResources;
import org.yang.iw.tool.material.IWToolMaterials;
import org.yang.iw.upgrade.IWUpgrades;

import static org.yang.iw.util.Base.iwlogger;


public class IWMain implements ModInitializer
{

	public static MinecraftServer getCurrentServer()
	{
		return currentServer;
	}

	public static IWPersistentData getPersistentData()
	{
		return persistentData;
	}

	private void initialize()
	{
		ServerLifecycleEvents.SERVER_STARTING.register(IWMain::onServerStarting);
		ServerLifecycleEvents.SERVER_STOPPED.register(IWMain::onServerStopped);
		ServerLifecycleEvents.SERVER_STARTED.register(IWMain::onServerStarted);
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
		IWToolMaterials.initialize();
	}

	private void initializeSequenced()
	{
		IWUpgrades.initialize(); // need itemGroups
		IWItems.initialize(); // need itemGroups
		IWBlocks.initialize(); // need itemGroups

		IWAbilities.initialize(); // need items
	}

	private void afterInitialize()
	{
		IWItemGroups.afterInitialize();
	}

	public static void serverStarting()
	{
		IWDamageTypes.boostrap(currentServer);
	}

	public static void serverStarted()
	{
		RandomBoostPool.boostrap();
		TableBoostPool.boostrap();
	}


	@Override
	public void onInitialize()
	{
		LoadTime.startInitialize();
		initialize();
		initializeSequenced();
		LoadTime.finishInitialize();
		afterInitialize();
	}

	private static MinecraftServer currentServer = null;
	private static IWPersistentData persistentData = null;

	private static void onServerStarting(MinecraftServer server)
	{
		iwlogger.info("Server: handle event onServerStarting");
		currentServer = server;
		LoadTime.serverStarting();
		IWMain.serverStarting();
		IWDataGen.clearPools();
	}

	private static void onServerStarted(MinecraftServer server)
	{
		iwlogger.info("Server: handle event onServerStarted");
		LoadTime.serverStarted();
		IWMain.serverStarted();
		persistentData = IWPersistentData.getServerState(currentServer);
	}

	private static void onServerStopped(MinecraftServer server)
	{
		iwlogger.info("Server: handle event onServerStopped");
		if (currentServer == server)
		{
			currentServer = null;
			persistentData = null;
		}
	}
}
