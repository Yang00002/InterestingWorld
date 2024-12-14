package org.yang.interestingworld.item.tool;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.IWUtil;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;
import org.yang.interestingworld.rune.IWRuneAbilitys;

import java.util.List;

import static org.yang.interestingworld.IWUtil.Components.currentEnergy;
import static org.yang.interestingworld.IWUtil.Components.maxEnergy;
import static org.yang.interestingworld.IWUtil.Return.*;
import static org.yang.interestingworld.IWUtil.TextStyle.PURE_GREEN_RGB;
import static org.yang.interestingworld.util.EnergyTool.*;
import static org.yang.interestingworld.util.RuneAbility.getAbility;
import static org.yang.interestingworld.util.RuneAbility.getColor;


public class EnergyToolItem extends ToolItem implements canSweeping, FabricItem
{
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}

	@Override
	public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack,
												  ItemStack newStack)
	{
		return !ItemStack.areItemsEqual(oldStack, newStack);
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return stack.contains(IWComponents.ABILITY_INDEX) || haveRealEnchantment(stack);
	}

	/**
	 * TwoSide
	 *
	 * @param remainingUseTicks (-INF, getMaxUseTime]
	 */
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
	{
		var ab = getAbility(stack);
		if (ab.canWork()) ab.usageTick(world, user, stack, remainingUseTicks);
	}

	/**
	 * TwoSide
	 */
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getStackInHand(hand);

		IWAbstractRuneAbility ab = getAbility(stack);
		if (ab.canWork())
		{
			byte res = ab.use(world, user, hand, stack);
			switch (res)
			{
				case PASS, IGNORE ->
				{
					return TypedActionResult.pass(stack);
				}
				case SUCCESS ->
				{
					return TypedActionResult.success(stack);
				}
				case FAIL ->
				{
					return TypedActionResult.fail(stack);
				}
				case CONSUME ->
				{
					user.setCurrentHand(hand);
					return TypedActionResult.consume(stack);
				}
			}
		}
		return TypedActionResult.pass(stack);
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user)
	{
		IWAbstractRuneAbility ab = getAbility(stack);
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
			var ab = getAbility(stack);
			if (ab.canWork()) ab.ServerInventoryTick(stack, world, entity, slot, selected);
		}
	}

	public EnergyToolItem(ToolMaterial material, Item.Settings settings)
	{
		super(material, settings.maxCount(1));
	}

	/**
	 * TwoSide
	 *
	 * @param remainingUseTicks (-INF, getMaxUseTime]
	 */
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		IWAbstractRuneAbility ab = getAbility(stack);
		if (ab.canWork()) ab.onStoppedUsing(stack, world, user, remainingUseTicks);
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
		IWAbstractRuneAbility ab = getAbility(stack);
		if (ab.canWork()) ab.postDamageEntity(stack, target, attacker);
		stack.damage(1, attacker, EquipmentSlot.MAINHAND);
	}

	private static void appendEneryText(MutableText text, ItemStack stack)
	{
		int max = (int) maxEnergy(stack);
		int cur = Math.clamp((int) currentEnergy(stack), 0, max);
		String num = String.valueOf(cur);
		text.append(Text.literal(num).withColor(MathHelper.hsvToRgb((float) cur / max / 3.0F, 1.0F, 1.0F)))
				.append(Text.literal(" / ").formatted(Formatting.GRAY))
				.append(Text.literal(String.valueOf(max)).withColor(PURE_GREEN_RGB));
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		MutableText text = Text.empty();
		if (stack.contains(IWComponents.MAX_ENERGY))
		{
			text.append(Text.translatable("tooltip.energy.count").formatted(Formatting.GRAY)).append(" ");
			appendEneryText(text, stack);
			tooltip.add(text);
		}
		var ab = getAbility(stack);
		if (ab.canWork()) ab.appendToolTip(tooltip);
		else ab.appendBanedToolTip(tooltip);
		if (ab.index != 0)
		{
			if (stack.hasEnchantments())
			{
				tooltip.add(Text.empty());
				if (onlyHaveDefaultEnchantment(stack))
					tooltip.add(Text.translatable("tooltip.defaultenchantment").withColor(IWUtil.TextStyle.GRAY_RGB));
			}
		}
		else
		{
			if (onlyHaveDefaultEnchantment(stack))
				tooltip.add(Text.translatable("tooltip.defaultenchantment").withColor(IWUtil.TextStyle.GRAY_RGB));
		}
	}

	@Override
	public Text getName(ItemStack stack)
	{
		var ab = getAbility(stack);
		if (ab == IWRuneAbilitys.DEFAULT_ABILITY)
			return Text.translatable(this.getTranslationKey(stack)).setStyle(IWUtil.TextStyle.BOLD_STYLE)
					.withColor(getLevelColor(stack));
		else return Text.translatable(this.getTranslationKey(stack)).setStyle(IWUtil.TextStyle.BOLD_STYLE)
				.withColor(getLevelColor(stack)).append(" ")
				.append(ab.getTitleText().setStyle(IWUtil.TextStyle.BOLD_STYLE).withColor(getColor(stack)));
	}


	@Override
	public boolean canSweep(ItemStack stack)
	{
		return false;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		IWAbstractRuneAbility ab = getAbility(stack);
		if (ab.canWork()) return ab.getUseAction(stack, UseAction.NONE);
		return UseAction.NONE;
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack)
	{
		IWAbstractRuneAbility ab = getAbility(stack);
		if (ab.canWork()) return ab.isUsedOnRelease(stack, false);
		return false;
	}
}
