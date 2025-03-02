package org.yang.iw.mixin.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.yang.iw.component.BoostableComponent;
import org.yang.iw.component.IWComponents;
import org.yang.iw.item.IWItemTags;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.mixin.helper.HelperAnvilScreenHandler;

import static org.yang.iw.mixin.helper.HelperAnvilScreenHandler.hitAddBoost;
import static org.yang.iw.util.IWCostUtil.getLevelFromExperience;

@Debug(export = true)
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

	public MixinAnvilScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory,
								   ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager)
	{
		super(type, syncId, playerInventory, context, forgingSlotsManager);
	}

	@Shadow
	public static int getNextCost(int cost)
	{
		return (int) Math.min((long) cost * 2L + 1L, 2147483647L);
	}

	@Inject(method = "updateResult", at = @At(value = "HEAD"), cancellable = true)
	private void mixinUpdateResult(CallbackInfo ci)
	{
		ItemStack itemStackLeft = input.getStack(0);
		if (itemStackLeft.isEmpty()) return;
		ItemStack itemStackRight = input.getStack(1);
		if (itemStackLeft.getItem() instanceof EnergyToolItem)
		{
			if (itemStackRight.isEmpty()) return;
			levelCost.set(0);
			ci.cancel();
			return;
		}
		if (itemStackLeft.isIn(IWItemTags.IS_BASE_HEART))
		{
			if (itemStackRight.isEmpty()) return;
			if (itemStackRight.getItem() == Items.ENCHANTED_BOOK)
			{
				var boostComponent = hitAddBoost(itemStackLeft, itemStackRight);
				if (boostComponent != null)
				{
					if (!boostComponent.isEmpty())
					{
						var boostable = itemStackLeft.getOrDefault(IWComponents.BOOSTABLE, BoostableComponent.DEFAULT);
						if (!boostable.isEmpty() &&
							boostable.remainBoostTime() < (boostComponent.level() + boostComponent.boostCount() - 1))
						{
							levelCost.set(0);
							ci.cancel();
							return;
						}
						ItemStack stackOut = itemStackLeft.copy();
						int cost = boostComponent.applyTo(stackOut);
						if (cost <= 0)
						{
							levelCost.set(0);
							ci.cancel();
							return;
						}
						cost = getLevelFromExperience(cost);
						if (!boostable.isEmpty()) stackOut.set(IWComponents.BOOSTABLE,
								boostable.hardUse(boostComponent.level() + boostComponent.boostCount() - 1));
						if (this.newItemName != null && !StringHelper.isBlank(this.newItemName))
						{
							if (!this.newItemName.equals(itemStackLeft.getName().getString()))
							{
								cost++;
								stackOut.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
							}
						}
						else if (itemStackLeft.contains(DataComponentTypes.CUSTOM_NAME))
						{
							cost++;
							stackOut.remove(DataComponentTypes.CUSTOM_NAME);
						}
						this.levelCost.set(cost);
						this.output.setStack(0, stackOut);
						this.sendContentUpdates();
						ci.cancel();
						return;
					}
				}
				else
				{
					this.levelCost.set(0);
					ci.cancel();
					return;
				}
			}
			else if (itemStackRight.getItem() != itemStackLeft.getItem() ||
					 !itemStackLeft.interestingWorld$getBoosts().isEmpty() ||
					 !itemStackRight.interestingWorld$getBoosts().isEmpty() ||
					 itemStackLeft.getEnchantments().isEmpty() || itemStackRight.getEnchantments().isEmpty())
			{
				this.levelCost.set(0);
				ci.cancel();
				return;
			}
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
				if (HelperAnvilScreenHandler.entryMatch(registryEntry, itemStackLeft))
				{
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
