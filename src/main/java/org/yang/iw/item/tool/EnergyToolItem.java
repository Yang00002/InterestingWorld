package org.yang.iw.item.tool;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.boost.pool.TableBoostPool;
import org.yang.iw.boost.pool.TableBoostPoolProvider;
import org.yang.iw.component.BoostableComponent;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.style.Color;
import org.yang.iw.util.style.TextStyle;

import java.util.List;

import static org.yang.iw.util.IWUtil.Components.currentEnergy;
import static org.yang.iw.util.IWUtil.Components.maxEnergy;
import static org.yang.iw.util.Return.*;
import static org.yang.iw.util.style.Color.getLevelColor;


public class EnergyToolItem extends Item implements TableBoostPoolProvider
{
	private final TableBoostPool tableBoostPool;

	@Override
	public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack,
												  ItemStack newStack)
	{
		return !ItemStack.areItemsEqual(oldStack, newStack) ||
			   oldStack.interestingWorld$getAbility().ability() != newStack.interestingWorld$getAbility().ability();
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return stack.contains(IWComponents.ABILITY) || !stack.interestingWorld$getBoosts().onlyDefault();
	}

	/**
	 * TwoSide
	 *
	 * @param remainingUseTicks (-INF, getMaxUseTime]
	 */
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		var ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork()) ab.usageTick(world, user, stack, remainingUseTicks);
	}

	/**
	 * TwoSide
	 */
	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getStackInHand(hand);
		AbstractAbility ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork())
		{
			byte res = ab.use(world, user, hand, stack);
			switch (res)
			{
				case PASS, IGNORE ->
				{
					return ActionResult.PASS;
				}
				case SUCCESS ->
				{
					return ActionResult.SUCCESS;
				}
				case FAIL ->
				{
					return ActionResult.FAIL;
				}
				case CONSUME ->
				{
					user.setCurrentHand(hand);
					return ActionResult.CONSUME;
				}
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user)
	{
		AbstractAbility ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork()) return ab.getMaxUseTime(stack, user, 0);
		return 0;
	}

	/**
	 * TwoSide
	 */
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if (!world.isClient() && entity instanceof PlayerEntity)
		{
			var ab = stack.interestingWorld$getAbility().ability();
			if (ab.canWork()) ab.ServerInventoryTick(stack, world, entity, slot, selected);
		}
	}

	public EnergyToolItem(Item.Settings settings, TableBoostPool pool)
	{
		super(settings.maxCount(1));
		this.tableBoostPool = pool;
	}

	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{
		AbstractAbility ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork()) return ab.finishUsing(stack, world, user);
		return super.finishUsing(stack, world, user);
	}

	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		AbstractAbility ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork()) return ab.onStoppedUsing(stack, world, user, remainingUseTicks);
		return super.onStoppedUsing(stack, world, user, remainingUseTicks);
	}

	/**
	 * ServerOly, require true to run postDamageEntity
	 */
	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		return true;
	}

	/**
	 * ServerOnly, would run only if postHit return true
	 */
	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		AbstractAbility ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork()) ab.postDamageEntity(stack, target, attacker);
		stack.damage(1, attacker, EquipmentSlot.MAINHAND);
	}

	private static void appendEnergyText(List<Text> tooltip, ItemStack stack)
	{
		MutableText text = Text.translatable(TranslationPool.TOOLTIP_ENERGYTOOL_COMMON_ENERGY)
				.formatted(Formatting.GRAY).append(" ");
		int max = (int) maxEnergy(stack);
		int cur = Math.clamp((int) currentEnergy(stack), 0, max);
		String num = String.valueOf(cur);
		text.append(Text.literal(num).withColor(MathHelper.hsvToRgb((float) cur / max / 3.0F, 1.0F, 1.0F)))
				.append(Text.literal(" / ").formatted(Formatting.GRAY))
				.append(Text.literal(String.valueOf(max)).withColor(Color.PURE_GREEN_RGB));
		float rate = stack.interestingWorld$getBoosts().getAcceleratedEnergyTransferRate(stack);
		text.append(" ")
				.append(Text.translatable(TranslationPool.TOOLTIP_ENERGYTOOL_ENERGY_RATE).withColor(Color.GRAY_RGB))
				.append(" ").append(Text.literal(TextStyle.FLOAT_FORMAT.format(rate))
						.withColor(MathHelper.hsvToRgb(Math.clamp(rate, 0, 1.0f) / 3.0F, 1.0F, 1.0F)));
		tooltip.add(text);
	}


	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		boolean add = false;
		if (stack.contains(IWComponents.MAX_ENERGY))
		{
			appendEnergyText(tooltip, stack);
			add = true;
		}
		if (!stack.getOrDefault(IWComponents.BOOSTABLE, BoostableComponent.DEFAULT).isEmpty())
		{
			int count = stack.getOrDefault(IWComponents.BOOSTABLE, BoostableComponent.DEFAULT).remainBoostTime();
			if (count < 1)
				tooltip.add(Text.translatable(TranslationPool.TOOLTIP_BOOSTTIME_OUT).withColor(Color.GRAY_RGB));
			else tooltip.add(Text.translatable(TranslationPool.TOOLTIP_REMAIN_BOOSTTIME).withColor(Color.GRAY_RGB)
					.append(Text.literal(String.valueOf(count)).withColor(getLevelColor(count))));
			add = true;
		}
		if (add) tooltip.add(Text.empty());
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		AbstractAbility ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork()) return ab.getUseAction(stack, UseAction.NONE);
		return UseAction.NONE;
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack)
	{
		AbstractAbility ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork()) return ab.isUsedOnRelease(stack, false);
		return false;
	}

	@Override
	public TableBoostPool getTableBoostPool()
	{
		return tableBoostPool;
	}
}
