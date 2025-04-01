package org.yang.iw.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ToolFlagComponent(int value)
{

	private static final int SWEEP_FLAG = 1;

	public static class Builder
	{
		int value;

		public Builder setCanSweep()
		{
			value |= SWEEP_FLAG;
			return this;
		}

		public ToolFlagComponent build()
		{
			return new ToolFlagComponent(value);
		}

		public void dump(ItemStack stack)
		{
			stack.set(IWComponents.TOOL_FLAG, build());
		}

		public void dump(Item.Settings settings)
		{
			settings.component(IWComponents.TOOL_FLAG, build());
		}
	}

	public static final ToolFlagComponent EMPTY = new ToolFlagComponent(0);

	private static final Codec<ToolFlagComponent> SIMPLE_CODEC = Codec.INT.xmap(ToolFlagComponent::new,
			ToolFlagComponent::value);
	private static final Codec<ToolFlagComponent> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.BOOL.optionalFieldOf("can_sweep", false).forGetter(ToolFlagComponent::canSweep))
			.apply(instance, ToolFlagComponent::createFromCodec));

	public static final Codec<ToolFlagComponent> CODEC = Codec.withAlternative(FULL_CODEC, SIMPLE_CODEC);

	public static PacketCodec<ByteBuf, ToolFlagComponent> PACKET_CODEC = PacketCodecs.INTEGER.xmap(
			ToolFlagComponent::new, ToolFlagComponent::value);

	public static ToolFlagComponent createFromCodec(boolean canSweep)
	{
		int flag = 0;
		if (canSweep) flag |= SWEEP_FLAG;
		return new ToolFlagComponent(flag);
	}

	public static ToolFlagComponent fromItemStack(ItemStack stack)
	{
		return stack.getOrDefault(IWComponents.TOOL_FLAG, EMPTY);
	}

	public Builder getBuilder()
	{
		var ret = new Builder();
		ret.value = value;
		return ret;
	}

	public static Builder builder()
	{
		return EMPTY.getBuilder();
	}

	public static Builder builder(ItemStack stack)
	{
		return fromItemStack(stack).getBuilder();
	}

	public boolean canSweep()
	{
		return (value & SWEEP_FLAG) > 0;
	}


}
