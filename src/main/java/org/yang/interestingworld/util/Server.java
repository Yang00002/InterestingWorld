package org.yang.interestingworld.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import org.yang.interestingworld.persistentdata.IWPersistentData;
import org.yang.interestingworld.resource.enchant.RuneEnchantData;
import org.yang.interestingworld.resource.enchant.SequencedEnchantResourceReloadListener;

import static org.yang.interestingworld.IWBlocks.addBlockItemToItemGroupWhenEnterWorld;
import static org.yang.interestingworld.IWItems.addItemToItemGroupWhenEnterWorld;
import static org.yang.interestingworld.resource.enchant.SequencedEnchantResourceReloadListener.handleRuneEnchantData;
import static org.yang.interestingworld.rune.IWRuneAbilitys.addRunesToItemGroup;
import static org.yang.interestingworld.util.Base.iwlogger;

public class Server
{
	private static MinecraftServer currentServer = null;

	private static IWPersistentData persistentData = null;

	private static void onServerStarting(MinecraftServer server)
	{
		iwlogger.info("Server: handle event onServerStarting");
		currentServer = server;
		var ow = server.getRegistryManager().getOptionalWrapper(RegistryKeys.ENCHANTMENT);
		ow.ifPresent(enchantmentImpl -> handleRuneEnchantData(server.getResourceManager(), enchantmentImpl));
		SequencedEnchantResourceReloadListener.handleReload(server.getResourceManager());
	}

	private static void onServerStarted(MinecraftServer server)
	{
		iwlogger.info("Server: handle event onServerStarted");
		persistentData = IWPersistentData.getServerState(currentServer);
		addItemToItemGroupWhenEnterWorld();
		addBlockItemToItemGroupWhenEnterWorld();
		addRunesToItemGroup();
	}

	private static void onServerStopped(MinecraftServer server)
	{
		iwlogger.info("Server: handle event onServerStopped");
		if (currentServer == server)
		{
			currentServer = null;
			persistentData = null;
			RuneEnchantData.clearData();
		}
	}

	public static MinecraftServer getCurrentServer()
	{
		return currentServer;
	}

	public static IWPersistentData getPersistentData()
	{
		return persistentData;
	}

	public static void initialize()
	{
		ServerLifecycleEvents.SERVER_STARTING.register(Server::onServerStarting);
		ServerLifecycleEvents.SERVER_STOPPED.register(Server::onServerStopped);
		ServerLifecycleEvents.SERVER_STARTED.register(Server::onServerStarted);

	}
}
