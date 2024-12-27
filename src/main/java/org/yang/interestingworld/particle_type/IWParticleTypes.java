package org.yang.interestingworld.particle_type;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.util.Base;

public class IWParticleTypes
{
	public static final SimpleParticleType SWEETCURSE_CYCLE = register("sweetcurse_cycle", true);
	public static final SimpleParticleType INFINITECURSE_CYCLE = register("infinitecurse_cycle", true);

	public static void initialize()
	{
	}

	private static SimpleParticleType register(String name, boolean alwaysShow)
	{
		return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Base.MOD_ID, name),
				FabricParticleTypes.simple(alwaysShow));
	}

	private static <T extends ParticleEffect> ParticleType<T> register(String name, ParticleType<T> particleType)
	{
		return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Base.MOD_ID, name), particleType);
	}
}
