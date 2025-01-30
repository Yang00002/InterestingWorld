package org.yang.iw;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.yang.iw.util.Base;

public class IWSounds
{

	public static SoundEvent ABILITYBAR_FULL = register("abilitybar_full");
	public static SoundEvent DOUBLE_SWEEP = register("double_sweep");

	private static SoundEvent register(String id)
	{
		SoundEvent ret = SoundEvent.of(Identifier.of(Base.MOD_ID, id));
		Registry.register(Registries.SOUND_EVENT, Identifier.of(Base.MOD_ID, id), ret);
		return ret;
	}

	public static void initialize()
	{

	}
}
