package org.yang.iw.ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.api.tag.IntrusiveTagHolder;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.util.Base;
import org.yang.iw.util.style.Color;

import static org.yang.iw.util.Return.IGNORE;

public class AbstractAbility implements IntrusiveTagHolder<AbilityHeart>
{
	private static final AbstractAbility DEFAULT = createInstance();
	final IntrusiveTag<AbilityHeart> tag;

	public boolean isEmpty()
	{
		return true;
	}

	private static AbstractAbility createInstance()
	{
		return new AbstractAbility(new IntrusiveTag<>());
	}

	public static AbstractAbility getDefault()
	{
		return DEFAULT;
	}

	AbstractAbility(IntrusiveTag<AbilityHeart> delegator)
	{
		tag = delegator;
	}

	public String id()
	{
		return "empty";
	}

	public Identifier identifier()
	{
		return Identifier.of(Base.MOD_ID, id());
	}

	public byte extractPower(MutableFloat value)
	{
		return IGNORE;
	}

	public Text getTip()
	{
		return Text.empty();
	}

	public void onServerAbilityOpen(ServerPlayerEntity player, IWServerPlayerData data)
	{
	}

	public void onServerAbilityClose(ServerPlayerEntity player, IWServerPlayerData data)
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

	public Text getTitleText()
	{
		return Text.empty();
	}

	public Text getBanedTitleText()
	{
		return Text.empty();
	}

	public boolean canApplyTo(ItemStack stack)
	{
		return false;
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

	public void onEnter(PlayerEntity entity, IWServerPlayerData manager)
	{
	}

	public void onLeave(PlayerEntity entity, IWServerPlayerData manager)
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
		return Color.WHITE_RGB;
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

	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{
		return stack;
	}

	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		return false;
	}

	public UseAction getUseAction(ItemStack stack, UseAction before)
	{
		return before;
	}

	public boolean isUsedOnRelease(ItemStack stack, boolean before)
	{
		return before;
	}

	public AbstractAbility getToolRenderAbility()
	{
		return null;
	}

	public void serverPlayerWeaponTick(PlayerEntity entity, IWServerPlayerData data, ItemStack stack)
	{

	}

	public void writeClientRenderDataToBuf(IWServerPlayerData data, RegistryByteBuf buf)
	{

	}

	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, ByteBuf buf)
	{

	}

	public boolean shouldRenderAbilityBar(IWClientPlayerData data)
	{
		return false;
	}

	public int abilityBarForegroundColor(IWClientPlayerData data)
	{
		return 0x282828;
	}

	public int abilityProcess(IWClientPlayerData data)
	{
		return 0;
	}

	public boolean shouldRenderAbilityText(IWClientPlayerData data)
	{
		return false;
	}

	public String abilityText(IWClientPlayerData data)
	{
		return "";
	}

	public int abilityTextColor()
	{
		return 0;
	}

	@Override
	public IntrusiveTag<AbilityHeart> asTag()
	{
		return tag;
	}

	public boolean canApplyTo(AbilityHeart heart)
	{
		return include(heart);
	}
}
