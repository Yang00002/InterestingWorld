package org.yang.interestingworld.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.joml.Quaternionf;

import static net.minecraft.client.particle.ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;

public class CycleParticle extends SpriteBillboardParticle
{
	private static final float HALF_PI = (float) Math.PI / 2;

	protected CycleParticle(ClientWorld clientWorld, double d, double e, double f)
	{
		super(clientWorld, d, e, f);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		this.alpha = Math.clamp(((float) (this.maxAge - this.age)) / this.maxAge, 0.0f, 1.0f);
		if (camera.getPos().y < this.y)
		{
			Quaternionf quaternionf = new Quaternionf();
			quaternionf.rotateX(HALF_PI);
			this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
		}
		else
		{
			Quaternionf quaternionf = new Quaternionf();
			quaternionf.rotateX(-HALF_PI);
			this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
		}
	}

	@Override
	public void tick()
	{
		if (this.age++ >= this.maxAge) this.markDead();
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getBrightness(float tint)
	{
		return 240;
	}

	public static class SweetCurseCycleFactory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public SweetCurseCycleFactory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType shriekParticleEffect, ClientWorld clientWorld, double d,
									   double e, double f, double g, double h, double i)
		{
			CycleParticle cycleParticle = new CycleParticle(clientWorld, d, e, f);
			cycleParticle.setSprite(this.spriteProvider);
			cycleParticle.scale = 3.0f;
			cycleParticle.setBoundingBoxSpacing(1.5f, 1.5f);
			cycleParticle.setMaxAge(30);
			cycleParticle.setAlpha(1.0F);
			return cycleParticle;
		}
	}

	public static class InfiniteCurseCycleFactory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public InfiniteCurseCycleFactory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType shriekParticleEffect, ClientWorld clientWorld, double d,
									   double e, double f, double g, double h, double i)
		{
			CycleParticle cycleParticle = new CycleParticle(clientWorld, d, e, f);
			cycleParticle.setSprite(this.spriteProvider);
			cycleParticle.scale = 4.0f;
			cycleParticle.setBoundingBoxSpacing(1.5f, 1.5f);
			cycleParticle.setMaxAge(30);
			cycleParticle.setAlpha(1.0F);
			return cycleParticle;
		}
	}
}
