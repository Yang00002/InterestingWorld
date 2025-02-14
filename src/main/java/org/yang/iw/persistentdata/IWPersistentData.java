package org.yang.iw.persistentdata;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.yang.iw.util.Base;

import static org.yang.iw.util.Base.iwlogger;


public class IWPersistentData extends PersistentState
{
	public Integer worldEnergyLevel = 0;

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
	{
		nbt.putInt("worldenergylevel", worldEnergyLevel);
		return nbt;
	}

	public static IWPersistentData createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
	{
		IWPersistentData state = new IWPersistentData();
		state.worldEnergyLevel = Math.clamp(tag.getInt("worldenergylevel"), 0, 10);
		return state;
	}

	private static final Type<IWPersistentData> type = new Type<>(IWPersistentData::new,
			IWPersistentData::createFromNbt, null
			// 此处理论上应为 'DataFixTypes' 的枚举，但我们直接传递为空(null)也可以
	);

	public static IWPersistentData getServerState(MinecraftServer server)
	{
		// (注：如需在任意维度生效，请使用 'World.OVERWORLD' ，不要使用 'World.END' 或 'World.NETHER')
		ServerWorld w = server.getWorld(World.OVERWORLD);
		if (w == null)
		{
			iwlogger.warn("No world for persistentData");
			return null;
		}
		PersistentStateManager persistentStateManager = w.getPersistentStateManager();
		return persistentStateManager.getOrCreate(type, Base.MOD_ID);
	}
}
