package org.yang.iw.item.tool;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.yang.iw.component.IWComponents;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.rune_ability.IWRuneAbilities;
import org.yang.iw.util.IWEnchantmentUtil;
import org.yang.iw.util.Server;
import org.yang.iw.util.style.Color;
import org.yang.iw.util.style.TextStyle;
import org.yang.iw.component.EnergyToolDataFlag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.yang.iw.util.IWRuneAbilityUtil.getAbility;
import static org.yang.iw.util.IWRuneAbilityUtil.getColor;
import static org.yang.iw.util.IWUtil.Components.currentEnergy;
import static org.yang.iw.util.IWUtil.Components.maxEnergy;
import static org.yang.iw.util.Return.*;


public class EnergyToolItem extends Item
{
	public final Map<Server.LoadOnceRegistryEntry<Enchantment>, Integer> defaultEnchantments;

	@Override
	public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack,
												  ItemStack newStack)
	{
		return !ItemStack.areItemsEqual(oldStack, newStack);
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return stack.contains(IWComponents.ABILITY_INDEX) ||
			   EnergyToolDataFlag.fromItemStack(stack).haveRealEnchantment();
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
	public ActionResult use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getStackInHand(hand);
		AbstractRuneAbility ab = getAbility(stack);
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
		AbstractRuneAbility ab = getAbility(stack);
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

	public EnergyToolItem(Item.Settings settings,
						  Map<Server.LoadOnceRegistryEntry<Enchantment>, Integer> defaultEnchantments)
	{
		super(settings.maxCount(1));
		this.defaultEnchantments = new HashMap<>();
	}

	/**
	 * TwoSide
	 *
	 * @param remainingUseTicks (-INF, getMaxUseTime]
	 */
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		AbstractRuneAbility ab = getAbility(stack);
		if (ab.canWork()) ab.onStoppedUsing(stack, world, user, remainingUseTicks);
		return true;
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
		AbstractRuneAbility ab = getAbility(stack);
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
				.append(Text.literal(String.valueOf(max)).withColor(Color.PURE_GREEN_RGB));
	}


	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
	{
		MutableText text = Text.empty();
		if (stack.contains(IWComponents.MAX_ENERGY))
		{
			text.append(Text.translatable(TranslationPool.TOOLTIP_ENERGYTOOL_COMMON_ENERGY).formatted(Formatting.GRAY))
					.append(" ");
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
				if (EnergyToolDataFlag.fromItemStack(stack).onlyHaveDefaultEnchantment())
					tooltip.add(Text.translatable(TranslationPool.TOOLTIP_DEFAULT_ENCHANT).withColor(Color.GRAY_RGB));
			}
		}
		else
		{
			if (EnergyToolDataFlag.fromItemStack(stack).onlyHaveDefaultEnchantment())
				tooltip.add(Text.translatable(TranslationPool.TOOLTIP_DEFAULT_ENCHANT).withColor(Color.GRAY_RGB));
		}
	}


	@Override
	public Text getName(ItemStack stack)
	{
		var ut = stack.getOrDefault(IWComponents.UPGRADE_TEXT, null);
		var ab = getAbility(stack);
		if (ut == null)
		{
			if (ab == IWRuneAbilities.DEFAULT_ABILITY)
				return Text.translatable(this.getTranslationKey()).setStyle(TextStyle.BOLD_STYLE)
						.withColor(EnergyToolDataFlag.fromItemStack(stack).levelColor());
			else return Text.translatable(this.getTranslationKey()).setStyle(TextStyle.BOLD_STYLE)
					.withColor(EnergyToolDataFlag.fromItemStack(stack).levelColor()).append(" ")
					.append(ab.getTitleText().setStyle(TextStyle.BOLD_STYLE).withColor(getColor(stack)));
		}
		else
		{
			if (ab == IWRuneAbilities.DEFAULT_ABILITY) return ut.copy().append(" ")
					.append(Text.translatable(this.getTranslationKey()).setStyle(TextStyle.BOLD_STYLE)
							.withColor(EnergyToolDataFlag.fromItemStack(stack).levelColor()));
			else return ut.copy().append(" ")
					.append(Text.translatable(this.getTranslationKey()).setStyle(TextStyle.BOLD_STYLE)
							.withColor(EnergyToolDataFlag.fromItemStack(stack).levelColor())).append(" ")
					.append(ab.getTitleText().setStyle(TextStyle.BOLD_STYLE).withColor(getColor(stack)));
		}
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		AbstractRuneAbility ab = getAbility(stack);
		if (ab.canWork()) return ab.getUseAction(stack, UseAction.NONE);
		return UseAction.NONE;
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack)
	{
		AbstractRuneAbility ab = getAbility(stack);
		if (ab.canWork()) return ab.isUsedOnRelease(stack, false);
		return false;
	}


	@Environment(EnvType.SERVER)
	public ItemStack getEnchantedDefaultItemStackFromServer()
	{
		ItemStack ret = getDefaultStack();
		if (defaultEnchantments == null) return ret;
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
				ItemEnchantmentsComponent.DEFAULT);
		defaultEnchantments.forEach((enchantContainer, level) -> {
			if (enchantContainer.get() != null && level > 0)
			{
				builder.set(enchantContainer.get(), level);
			}
		});
		var ecs = builder.build();
		if (!ecs.isEmpty()) IWEnchantmentUtil.setDefaultEnchant(ret, ecs);
		return ret;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getEnchantedDefaultItemStackFromClient(RegistryWrapper<Enchantment> wrapper)
	{
		ItemStack ret = getDefaultStack();
		if (defaultEnchantments == null) return ret;
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
				ItemEnchantmentsComponent.DEFAULT);
		defaultEnchantments.forEach((enchantContainer, level) -> {
			if (enchantContainer.get() != null && level > 0)
			{
				var ec = enchantContainer.get();
				var eck = ec.getKey();
				if (eck.isPresent())
				{
					var wec = wrapper.getOptional(eck.get());
					wec.ifPresent(enchantmentReference -> builder.add(enchantmentReference, level));
				}
			}
		});
		var ecs = builder.build();
		if (!ecs.isEmpty()) IWEnchantmentUtil.setDefaultEnchant(ret, ecs);
		return ret;
	}
}
