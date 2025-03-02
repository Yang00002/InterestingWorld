package org.yang.iw.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BoostableComponent(int max, int current)
{
	public static final BoostableComponent DEFAULT = new BoostableComponent(0, 0);
	private static final Codec<BoostableComponent> FULL_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Codec.INT.fieldOf("max").forGetter(component -> component.max),
							Codec.INT.optionalFieldOf("current", -1).forGetter(component -> component.current))
					.apply(instance, BoostableComponent::createForCODEC));

	public static final Codec<BoostableComponent> CODEC = Codec.withAlternative(FULL_CODEC, Codec.INT,
			v -> createForCODEC(v, v));
	public static PacketCodec<RegistryByteBuf, BoostableComponent> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, BoostableComponent::max, PacketCodecs.INTEGER, BoostableComponent::current,
			BoostableComponent::new);

	public static BoostableComponent createForCODEC(int max, int cur)
	{
		if (max <= 0) return DEFAULT;
		if (cur < 0 || max < cur) return new BoostableComponent(max, max);
		return new BoostableComponent(max, cur);
	}

	public boolean isEmpty()
	{
		return this == DEFAULT;
	}

	public int remainBoostTime()
	{
		return current;
	}

	public static BoostableComponent of(int max)
	{
		return new BoostableComponent(max, max);
	}

	public BoostableComponent hardUse(int count)
	{
		count = Math.min(count, current);
		return new BoostableComponent(max - count, current - count);
	}

	public BoostableComponent use(int count)
	{
		count = Math.min(count, current);
		return new BoostableComponent(max, current - count);
	}

	public BoostableComponent clear()
	{
		return new BoostableComponent(max, max);
	}
}
