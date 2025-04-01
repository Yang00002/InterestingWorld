package org.yang.iw.api.register;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public class KEPair<T>
{
	protected RegistryKey<T> key;
	protected RegistryEntry<T> entry = null;

	public KEPair(RegistryKey<T> key)
	{
		this.key = key;
	}

	public RegistryKey<T> key()
	{
		return key;
	}

	public RegistryEntry<T> entry()
	{
		return entry;
	}
}
