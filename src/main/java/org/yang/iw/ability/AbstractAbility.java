package org.yang.iw.ability;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.yang.iw.IWRegistries;
import org.yang.iw.api.io.BytesReader;
import org.yang.iw.api.io.BytesWriter;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.api.tag.IntrusiveTagHolder;
import org.yang.iw.entity.player.AbilityBarType;
import org.yang.iw.entity.player.IWClientPlayerData;
import org.yang.iw.entity.player.IWServerPlayerData;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.network.bitio.BitReader;
import org.yang.iw.network.bitio.BitWriter;
import org.yang.iw.util.Base;
import org.yang.iw.util.style.Color;

import java.io.IOException;

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

	public AbilityCooldownGroup getCooldownGroup()
	{
		return AbilityCooldownGroup.EMPTY;
	}

	AbstractAbility(IntrusiveTag<AbilityHeart> delegator)
	{
		tag = delegator;
	}

	public String id()
	{
		return "empty";
	}

	public void onClientTickOver(ClientPlayerEntity player, IWClientPlayerData data)
	{

	}

	public void onServerTickOver(ServerPlayerEntity player, IWServerPlayerData data)
	{

	}

	public static AbstractAbility readNbt(NbtCompound compound, String key)
	{
		var ar = compound.getByteArray(key);
		if (ar.length == 0) return AbstractAbility.getDefault();
		BytesReader reader = new BytesReader(ar);
		var id = reader.readIdentifier();
		var ret = IWRegistries.ABILITY.get(id);
		return ret == null ? AbstractAbility.getDefault() : ret;
	}

	public final void writeNbt(NbtCompound compound, String key)
	{
		Identifier identifier = identifier();
		String namespace = identifier.getNamespace();
		String path = identifier.getPath();
		int length = namespace.length() + path.length() + 1;
		BytesWriter writer = new BytesWriter(length);
		try
		{
			writer.writeIdentifier(identifier);
			compound.putByteArray(key, writer.toByteArray());
		} catch (IOException ignored)
		{
		}
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

	public void writeClientRenderDataToBuf(IWServerPlayerData data, BitWriter buf)
	{

	}

	public void readClientRenderDataFromBuf(PlayerEntity entity, IWClientPlayerData data, BitReader buf)
	{

	}

	public int abilityBarForegroundColor(IWClientPlayerData data)
	{
		return getColor();
	}

	public int abilityProcess(IWClientPlayerData data)
	{
		return 16;
	}

	public String abilityText(IWClientPlayerData data)
	{
		return null;
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
