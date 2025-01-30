package org.yang.iw.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import org.yang.iw.datagen.IWDataGen;
import org.yang.iw.enchant.EnchantResourceReloader;
import org.yang.iw.enchant.resource.EnchantData;
import org.yang.iw.persistentdata.IWPersistentData;

import java.util.ArrayList;
import java.util.List;

import static org.yang.iw.util.Base.iwlogger;

public class Server
{
	// 用于在服务器启动时自动从 registryKey 获得 registryEntry. 这样获取的 entry 应当只在服务器启动时加载一次，并且应当无论启动什么服务器都保持存在, 例如内置的伤害类型和附魔。
	public static class LoadOnceRegistryEntry<T>
	{
		RegistryEntry<T> registryEntry;
		final RegistryKey<T> registryKey;
		final RegistryKey<Registry<T>> wrapperType;

		LoadOnceRegistryEntry(RegistryKey<T> key, RegistryKey<Registry<T>> registryType)
		{
			registryKey = key;
			wrapperType = registryType;
			registryEntry = null;
		}

		void pop()
		{
			registryEntry = null;
		}

		void push(DynamicRegistryManager manager)
		{
			var wrapperOp = manager.getOptionalWrapper(wrapperType);
			if (wrapperOp.isPresent())
			{
				var wrapper = wrapperOp.get();
				var e = wrapper.getOptional(registryKey);
				registryEntry = e.orElse(null);
				if (registryEntry != null) iwlogger.info(registryEntry.getIdAsString());
			}
			else registryEntry = null;
		}

		public RegistryEntry<T> get()
		{
			return registryEntry;
		}

		public boolean isPresent()
		{
			return registryEntry != null;
		}
	}

	private static final List<LoadOnceRegistryEntry<?>> loadOnceRegistryEntries = new ArrayList<>();

	public static <T> LoadOnceRegistryEntry<T> getLoadOnceRegistryEntry(RegistryKey<T> key,
																		RegistryKey<Registry<T>> registryType)
	{
		if (currentServer == null)
		{
			var ret = new LoadOnceRegistryEntry<>(key, registryType);
			loadOnceRegistryEntries.add(ret);
			return ret;
		}
		else throw new RuntimeException("不应在服务器启动后注册新的 LoadOnceRegistryEntry");
	}

	private static MinecraftServer currentServer = null;

	private static IWPersistentData persistentData = null;

	private static void onServerStarting(MinecraftServer server)
	{
		iwlogger.info("Server: handle event onServerStarting");
		currentServer = server;
		var manager = server.getRegistryManager();
		for (var entry : loadOnceRegistryEntries)
		{
			entry.push(manager);
		}
		var ow = manager.getOptionalWrapper(RegistryKeys.ENCHANTMENT);
		ow.ifPresent(enchantmentImpl -> EnchantData.initialize(server.getResourceManager(), enchantmentImpl));
		EnchantResourceReloader.handleReload(server.getResourceManager());
		IWDataGen.clearPools();
	}

	private static void onServerStarted(MinecraftServer server)
	{
		iwlogger.info("Server: handle event onServerStarted");
		persistentData = IWPersistentData.getServerState(currentServer);
	}

	private static void onServerStopped(MinecraftServer server)
	{
		iwlogger.info("Server: handle event onServerStopped");
		if (currentServer == server)
		{
			for (var entry : loadOnceRegistryEntries)
				entry.pop();
			currentServer = null;
			persistentData = null;
			EnchantData.clearData();
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
