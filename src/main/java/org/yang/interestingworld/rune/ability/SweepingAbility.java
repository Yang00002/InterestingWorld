package org.yang.interestingworld.rune.ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.playerdatamanager.ClientPlayerDataManager;
import org.yang.interestingworld.playerdatamanager.ServerPlayerDataManager;
import org.yang.interestingworld.rune.IWRuneAbility;

import java.util.List;

import static org.yang.interestingworld.IWUtil.TextStyle.WHITE_RGB;

public class SweepingAbility extends IWRuneAbility
{
	@Override
	public void appendToolTip(List<Text> tooltip)
	{
		super.appendToolTip(tooltip);
		tooltip.add(Text.translatable("sweeping_ability_detail").withColor(getColor()));
	}

	@Override
	public MutableText getTitleText()
	{
		return Text.translatable("sweeping_ability_title");
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		Item it = stack.getItem();
		if (it instanceof EnergyToolItem)
		{
			return !((EnergyToolItem) it).canSweep(stack);
		}
		return false;
	}

	public void writeClientRenderDataToBuf(ServerPlayerDataManager data, RegistryByteBuf buf)
	{
		buf.writeBoolean(data.charged);
	}

	@Override
	public void readClientRenderDataFromBuf(PlayerEntity entity, ClientPlayerDataManager data, ByteBuf buf)
	{
		boolean e = buf.readBoolean();
		if (e && !data.energy_enough_to_use)
		{
			playChargedOverSound(entity);
		}
		data.energy_enough_to_use = e;
	}

	@Override
	public boolean canSweeping(ItemStack stack, PlayerEntity entity, ServerPlayerDataManager data)
	{
		if (entity instanceof ServerPlayerEntity player) return data.extractAutomicEnergy(stack, player, 1);
		return false;
	}

	@Override
	public int abilityBarForegroundColor(ClientPlayerDataManager data)
	{
		return WHITE_RGB;
	}

	@Override
	public void serverPlayerWeaponTick(PlayerEntity entity, ServerPlayerDataManager data, ItemStack stack)
	{
		if (data.getCurrentEnergy() < 1.0f)
		{
			boolean c = data.tryExtractAutomicEnergy(stack, (ServerPlayerEntity) entity, 1.0f);
			if (c != data.charged) data.shouldSync = true;
			data.charged = c;
		}
	}

	public int abilityProcess(ClientPlayerDataManager data)
	{
		if (data.energy_enough_to_use || data.shown_energy > 0) return 16;
		return 0;
	}

	public void onEnter(PlayerEntity entity, ServerPlayerDataManager manager)
	{
		manager.charged = false;
	}

	public void onLeave(PlayerEntity entity, ServerPlayerDataManager manager)
	{
		manager.charged = false;
	}

}
