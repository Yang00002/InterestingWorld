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
import org.yang.iw.upgrade.AbstractUpgrade;

import java.util.List;

public record UpgradeComponent(AbstractUpgrade upgrade)
{
	public static final UpgradeComponent DEFAULT = new UpgradeComponent(AbstractUpgrade.getDefault()); // 类加载顺序规定

	public static final Codec<UpgradeComponent> CODEC = IWRegistries.UPGRADE.getCodec()
			.xmap(UpgradeComponent::new, UpgradeComponent::upgrade);
	public static final PacketCodec<RegistryByteBuf, UpgradeComponent> PACKET_CODEC = PacketCodecs.registryValue(
			IWRegistryKeys.UPGRADE).xmap(UpgradeComponent::new, UpgradeComponent::upgrade);

	public boolean isEmpty()
	{
		return upgrade.isEmpty();
	}

	public void appendTooltip(Item.TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		if (!upgrade.isEmpty()) tooltip.add(upgrade.getTitleText());
	}

	public void appendDetailedTooltip(Item.TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		if (!upgrade.isEmpty())
		{
			tooltip.add(upgrade.getTitleText());
			tooltip.add(upgrade.getTip());
		}
	}
}
