package org.yang.iw.component;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import org.yang.iw.IWRegistries;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.ability.AbstractAbility;

import java.util.List;

public record AbilityComponent(AbstractAbility ability)
{
	public static final AbilityComponent DEFAULT = new AbilityComponent(AbstractAbility.getDefault()); // 类加载顺序规定

	public static final Codec<AbilityComponent> CODEC = IWRegistries.ABILITY.getCodec()
			.xmap(AbilityComponent::new, AbilityComponent::ability);
	public static final PacketCodec<RegistryByteBuf, AbilityComponent> PACKET_CODEC = PacketCodecs.registryValue(
			IWRegistryKeys.ABILITY).xmap(AbilityComponent::new, AbilityComponent::ability);

	public boolean isEmpty()
	{
		return ability.isEmpty();
	}

	public void appendTooltip(Item.TooltipContext context, List<Text> tooltip, TooltipType type, int worldLevel)
	{
		if (!ability.isEmpty())
			tooltip.add(worldLevel >= ability.level() ? ability.getTitleText() : ability.getBanedTitleText());
	}

	public void appendDetailedTooltip(Item.TooltipContext context, List<Text> tooltip, TooltipType type,
									  int worldLevel)
	{
		if (!ability.isEmpty())
		{
			tooltip.add(worldLevel >= ability.level() ? ability.getTitleText() : ability.getBanedTitleText());
			tooltip.add(ability.getTip());
		}
	}
}
