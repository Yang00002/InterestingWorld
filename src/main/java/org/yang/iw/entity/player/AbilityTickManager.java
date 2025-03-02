package org.yang.iw.entity.player;

import net.minecraft.nbt.NbtCompound;

public class AbilityTickManager
{
	AbilityTickManager(IWServerPlayerData data)
	{
		this.data = data;
	}

	public void copy(AbilityTickManager manager)
	{
		started = manager.started;
		maxTick = manager.maxTick;
		currentTick = manager.currentTick;
	}

	public void readNbt(NbtCompound nbt)
	{
		maxTick = nbt.getInt("max");
		currentTick = nbt.getInt("current");
		started = nbt.getBoolean("started");
	}

	public NbtCompound writeNbt()
	{
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putInt("max", maxTick);
		nbtCompound.putInt("current", currentTick);
		nbtCompound.putBoolean("started", started);
		return nbtCompound;
	}

	private boolean started = false;
	private final IWServerPlayerData data;
	int maxTick = 0;
	int currentTick = 0;

	public int tickProgress()
	{
		return maxTick > 0 ? (currentTick << 4) / maxTick : 16;
	}

	public void tick()
	{
		if (started && currentTick < maxTick)
		{
			currentTick++;
			if (currentTick == maxTick) data.setTickOver(true);
			switch (data.barType)
			{
				case TICK ->
				{
					if (tickProgress() != data.clientStep) data.sync();
				}
				case TICK_REVERSE ->
				{
					if (tickProgress() != 16 - data.clientStep) data.sync();
				}
			}
		}
	}

	public boolean isOn()
	{
		return started;
	}

	public void pause()
	{
		started = false;
	}

	public void start()
	{
		started = true;
	}

	public void reset(int tick)
	{
		maxTick = tick;
		currentTick = 0;
		if (maxTick > 0) switch (data.barType)
		{
			case TICK ->
			{
				if (0 != data.clientStep) data.sync();
			}
			case TICK_REVERSE ->
			{
				if (16 != data.clientStep) data.sync();
			}
		}
		started = false;
	}

	public void resetAndStart(int tick)
	{
		maxTick = tick;
		currentTick = 0;
		if (maxTick > 0) switch (data.barType)
		{
			case TICK ->
			{
				if (0 != data.clientStep) data.sync();
			}
			case TICK_REVERSE ->
			{
				if (16 != data.clientStep) data.sync();
			}
		}
		started = true;
	}
}
