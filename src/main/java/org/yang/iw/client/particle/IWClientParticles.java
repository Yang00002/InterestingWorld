package org.yang.iw.client.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import org.yang.iw.particle_type.IWParticleTypes;

public class IWClientParticles
{

	public static void initialize()
	{
		ParticleFactoryRegistry.getInstance()
				.register(IWParticleTypes.SWEETCURSE_CYCLE, CycleParticle.SweetCurseCycleFactory::new);
		ParticleFactoryRegistry.getInstance()
				.register(IWParticleTypes.INFINITECURSE_CYCLE, CycleParticle.InfiniteCurseCycleFactory::new);
	}
}
