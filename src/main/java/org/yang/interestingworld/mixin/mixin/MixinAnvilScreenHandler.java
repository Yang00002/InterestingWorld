package org.yang.interestingworld.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.interestingworld.IWComponents;
import org.yang.interestingworld.item.IWItems;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.util.style.Color;
import org.yang.interestingworld.util.style.TextStyle;

@Mixin(AnvilScreenHandler.class)
public abstract class MixinAnvilScreenHandler extends ForgingScreenHandler
{
	@Shadow
	@Final
	private Property levelCost;

	@Shadow
	private @Nullable String newItemName;

	@Shadow
	private int repairItemUsage;

	@Shadow
	public static int getNextCost(int cost)
	{
		return (int) Math.min((long) cost * 2L + 1L, 2147483647L);
	}

	public MixinAnvilScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory,
								   ScreenHandlerContext context)
	{
		super(type, syncId, playerInventory, context);
	}

	@Accessor("newItemName")
	abstract String accessNewItemName();

	@Redirect(method = "setNewItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;literal" +
																			 "(Ljava/lang/String;)" +
																			 "Lnet/minecraft/text/MutableText;"))
	private MutableText mixinSetNewItemName(String string, @Local ItemStack itemStack)
	{
		if (itemStack.getItem() instanceof EnergyToolItem) return Text.literal(string).setStyle(TextStyle.BOLD_STYLE)
				.withColor(itemStack.getOrDefault(IWComponents.ITEM_COLOR, Color.WHITE_RGB));
		else return Text.literal(string);
	}

	@Inject(method = "updateResult", at = @At(value = "HEAD"), cancellable = true)
	private void mixinUpdateResult(CallbackInfo ci)
	{

		ItemStack itemStackLeft = input.getStack(0);
		ItemStack itemStackRight = input.getStack(1);
		if (itemStackLeft.isEmpty())
		{
			this.output.setStack(0, ItemStack.EMPTY);
			this.levelCost.set(0);
			ci.cancel();
			return;
		}
		if (itemStackLeft.getItem() instanceof EnergyToolItem)
		{
			this.levelCost.set(0);
			ci.cancel();
		}
		else if (itemStackLeft.getItem() == IWItems.EMPTY_RUNE)
		{
			if (itemStackRight.isEmpty()) return;
			if (itemStackRight.getItem() != IWItems.EMPTY_RUNE) return;
			if (!itemStackLeft.hasEnchantments() || !itemStackRight.hasEnchantments()) return;
			this.levelCost.set(1);
			int repairCostLevel = 0;
			long l = 0L;
			int j = 0;
			// 有左端物品并且其可以附魔
			ItemStack copyOfStackLeft = itemStackLeft.copy();
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
					EnchantmentHelper.getEnchantments(copyOfStackLeft));
			l += (long) itemStackLeft.getOrDefault(DataComponentTypes.REPAIR_COST, 0) +
				 (long) itemStackRight.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
			this.repairItemUsage = 0;
			// 右侧附魔
			ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(itemStackRight);
			boolean haveAcceptEnchant = false;
			boolean haveCanNotAcceptEnchant = false;

			for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry :
					itemEnchantmentsComponent.getEnchantmentEntries())
			{
				RegistryEntry<Enchantment> registryEntry = entry.getKey();
				int leftLevel = builder.getLevel(registryEntry);
				int rightLevel = entry.getIntValue();
				rightLevel = leftLevel == rightLevel ? rightLevel + 1 : Math.max(rightLevel, leftLevel);
				Enchantment enchantment = registryEntry.value();
				boolean leftAcceptEnchant = enchantment.isAcceptableItem(itemStackLeft);
				if (this.player.getAbilities().creativeMode)
				{
					leftAcceptEnchant = true;
				}
				for (RegistryEntry<Enchantment> registryEntry2 : builder.getEnchantments())
				{
					if (!registryEntry2.equals(registryEntry) &&
						!Enchantment.canBeCombined(registryEntry, registryEntry2))
					{
						leftAcceptEnchant = false;
						repairCostLevel++;
					}
				}

				if (!leftAcceptEnchant)
				{
					haveCanNotAcceptEnchant = true;
				}
				else
				{
					haveAcceptEnchant = true;
					if (rightLevel > enchantment.getMaxLevel())
					{
						rightLevel = enchantment.getMaxLevel();
					}

					builder.set(registryEntry, rightLevel);
					int s = enchantment.getAnvilCost();

					repairCostLevel += s * rightLevel;
					if (itemStackLeft.getCount() > 1)
					{
						repairCostLevel = 40;
					}
				}
			}

			if (haveCanNotAcceptEnchant && !haveAcceptEnchant)
			{
				this.output.setStack(0, ItemStack.EMPTY);
				this.levelCost.set(0);
				return;
			}


			if (this.newItemName != null && !StringHelper.isBlank(this.newItemName))
			{
				if (!this.newItemName.equals(itemStackLeft.getName().getString()))
				{
					j = 1;
					repairCostLevel += j;
					copyOfStackLeft.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
				}
			}
			else if (itemStackLeft.contains(DataComponentTypes.CUSTOM_NAME))
			{
				j = 1;
				repairCostLevel += j;
				copyOfStackLeft.remove(DataComponentTypes.CUSTOM_NAME);
			}

			int t = (int) MathHelper.clamp(l + (long) repairCostLevel, 0L, 2147483647L);
			this.levelCost.set(t);
			if (repairCostLevel <= 0)
			{
				copyOfStackLeft = ItemStack.EMPTY;
			}

			if (j == repairCostLevel && j > 0 && this.levelCost.get() >= 40)
			{
				this.levelCost.set(39);
			}

			if (this.levelCost.get() >= 40 && !this.player.getAbilities().creativeMode)
			{
				copyOfStackLeft = ItemStack.EMPTY;
			}

			if (!copyOfStackLeft.isEmpty())
			{
				int kxx = copyOfStackLeft.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
				if (kxx < itemStackRight.getOrDefault(DataComponentTypes.REPAIR_COST, 0))
				{
					kxx = itemStackRight.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
				}

				if (j != repairCostLevel || j == 0)
				{
					kxx = getNextCost(kxx);
				}

				copyOfStackLeft.set(DataComponentTypes.REPAIR_COST, kxx);
				EnchantmentHelper.set(copyOfStackLeft, builder.build());
			}

			this.output.setStack(0, copyOfStackLeft);
			this.sendContentUpdates();
			ci.cancel();
		}
	}
}
