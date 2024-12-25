package org.yang.interestingworld.client;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import org.yang.interestingworld.IWParticleTypes;
import org.yang.interestingworld.client.particle.CycleParticle;

public class IWClientParticles
{

	static void initialize()
	{
		ParticleFactoryRegistry.getInstance()
				.register(IWParticleTypes.SWEETCURSE_CYCLE, CycleParticle.SweetCurseCycleFactory::new);
		ParticleFactoryRegistry.getInstance()
				.register(IWParticleTypes.INFINITECURSE_CYCLE, CycleParticle.InfiniteCurseCycleFactory::new);
	}
}
