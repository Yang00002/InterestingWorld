package org.yang.interestingworld.rune;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.playerdatamanager.ClientPlayerDataManager;
import org.yang.interestingworld.playerdatamanager.ServerPlayerDataManager;

import java.util.List;

import static org.yang.interestingworld.IWUtil.Return.IGNORE;
import static org.yang.interestingworld.IWUtil.TextStyle.WHITE_RGB;

public class IWAbstractRuneAbility
{
	/**
	 * Do not modify after registered!
	 */
	public short index = 0;
	public short runeIndex = 0;
	public short toolIndex = 0;

	public byte extractPower(IWUtil.Return.FloatHolder value)
	{
		return IGNORE;
	}

	public void appendToolTip(List<Text> tooltip)
	{
	}

	/**
	 * TwoSide
	 * <p>
	 * CallSequence:
	 * <p>
	 * use(setCurrentItem) -> usageTick... -> onStoppedUsing -> usageTick...
	 *
	 * @param remainingUseTicks (-INF, getMaxUseTime]
	 */
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{

	}

	public MutableText getTitleText()
	{
		return Text.empty();
	}

	public boolean canApplyTo(ItemStack stack)
	{
		return false;
	}

	public void appendBanedToolTip(List<Text> tooltip)
	{
	}

	/**
	 * ServerOnly
	 */
	public void ServerInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
	}
	/**
	 * ClientOnly
	 */
	public boolean canEnergyItemBarVisible()
	{
		return true;
	}

	public void onRemoveAbility(ItemStack stack)
	{
	}

	public void onSetAbility(ItemStack stack)
	{

	}

	public void onEnter(PlayerEntity entity, ServerPlayerDataManager manager)
	{
	}

	public void onLeave(PlayerEntity entity, ServerPlayerDataManager manager)
	{

	}

	public int level()
	{
		return 0;
	}

	/**
	 * TwoSide
	 */
	public boolean canWork()
	{
		return false;
	}

	public int getColor()
	{
		return WHITE_RGB;
	}

	/**
	 * ServerOnly
	 */
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{

	}

	/**
	 * TwoSide
	 */
	public void atSweeping(ItemStack stack, PlayerEntity entity)
	{

	}

	/**
	 * TwoSide
	 * <p>
	 * CallSequence:
	 * <p>
	 * use(setCurrentItem) -> usageTick... -> onStoppedUsing -> usageTick...
	 */
	public byte use(World world, PlayerEntity user, Hand hand, ItemStack stack)
	{
		return IGNORE;
	}

	public int getMaxUseTime(ItemStack stack, LivingEntity user, int before)
	{
		return before;
	}

	/**
	 * TwoSide
	 * <p>
	 * CallSequence:
	 * <p>
	 * use(setCurrentItem) -> usageTick... -> onStoppedUsing -> usageTick...
	 *
	 * @param remainingUseTicks (-INF, getMaxUseTime]
	 */
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
	}

	public UseAction getUseAction(ItemStack stack, UseAction before)
	{
		return before;
	}

	public boolean isUsedOnRelease(ItemStack stack, boolean before)
	{
		return before;
	}

	public IWAbstractRuneAbility getRuneIndexParent()
	{
		return null;
	}

	public IWAbstractRuneAbility getToolIndexParent()
	{
		return null;
	}

	public void serverPlayerWeaponTick(PlayerEntity entity, ServerPlayerDataManager data, ItemStack stack)
	{

	}

	public void writeClientRenderDataToBuf(ServerPlayerDataManager data, RegistryByteBuf buf)
	{

	}

	public void readClientRenderDataFromBuf(PlayerEntity entity, ClientPlayerDataManager data, ByteBuf buf)
	{

	}

	public boolean shouldRenderAbilityBar(ClientPlayerDataManager data)
	{
		return false;
	}

	public int abilityBarForegroundColor(ClientPlayerDataManager data)
	{
		return 0x282828;
	}

	public int abilityProcess(ClientPlayerDataManager data)
	{
		return 0;
	}

	public boolean shouldRenderAbilityNumber(ClientPlayerDataManager data)
	{
		return false;
	}

	public int abilityNumber(ClientPlayerDataManager data)
	{
		return 0;
	}

	public int abilityNumberColor()
	{
		return 0;
	}
}
