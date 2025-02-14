package org.yang.iw.block.forgingblock;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.IWResources;
import org.yang.iw.IWScreenHandlers;
import org.yang.iw.block.IWBlocks;
import org.yang.iw.component.EnergyToolDataFlag;
import org.yang.iw.component.HeartDataFlag;
import org.yang.iw.component.IWComponents;
import org.yang.iw.enchant.util.TableEnchantGenerator;
import org.yang.iw.item.IWItems;
import org.yang.iw.item.heart.AbstractHeart;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.item.upgrade.UpgradeTemplate;
import org.yang.iw.rune_ability.AbstractRuneAbility;
import org.yang.iw.rune_ability.IWRuneAbilities;
import org.yang.iw.util.IWEnchantmentUtil;
import org.yang.iw.util.Rand;
import org.yang.iw.util.Server;

import java.util.HashMap;
import java.util.Map;

import static org.yang.iw.util.IWEnchantmentUtil.*;
import static org.yang.iw.util.IWRuneAbilityUtil.*;

public class ForgingBlockScreenHandler extends ScreenHandler
{

	private static final RepairableComponent DEFUALT_REPAIR_INGREDIENT = new RepairableComponent(
			RegistryEntryList.of());
	// <editor-fold desc="数据段">
	private final boolean clientSide;
	private final ScreenHandlerContext context;
	private final ForgingBlockInventory inventoryUp; // 0
	private final ForgingBlockInventory inventoryDown; // 1
	private final ForgingBlockInventory inventoryIngredient; // 3 - 11
	private final Inventory inventoryOut; // 2
	private int random_seed;

	// player pack 12 - 20

	// player hotbar 21 - 47

	private final Property globalState;
	private final Property experienceCost;
	private RepairableComponent repairIngredient;
	private final Property repairCount;
	private final Rand rand = new Rand();

	// </editor-fold>

	//<editor-fold desc="初始化">

	public ForgingBlockScreenHandler(int syncId, PlayerInventory inventory)
	{
		this(syncId, inventory, ScreenHandlerContext.EMPTY);
	}

	public ForgingBlockScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context)
	{
		super(IWScreenHandlers.FORGINGBLOCK_SCREEN_HANDLER, syncId);
		this.context = context;
		this.clientSide = !(playerInventory.player instanceof ServerPlayerEntity);
		if (playerInventory.player instanceof ServerPlayerEntity)
		{
			var data = ((ServerPlayerEntity) playerInventory.player).getIWServerPlayerData();
			this.random_seed = data.forging_seed;
		}
		else this.random_seed = 0;
		this.inventoryUp = createInventoryUp();
		this.inventoryDown = createInventoryDown();
		this.inventoryOut = createInventoryOut();
		this.inventoryIngredient = createInventoryIngredient();
		this.addPlayerInventorySlots(playerInventory);
		globalState = net.minecraft.screen.Property.create();
		this.addProperty(globalState);
		globalState.set(0);
		experienceCost = net.minecraft.screen.Property.create();
		this.addProperty(experienceCost);
		experienceCost.set(0);
		repairCount = net.minecraft.screen.Property.create();
		this.addProperty(repairCount);
		repairCount.set(0);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot)
	{
		ItemStack itemStack = ItemStack.EMPTY;
		Slot fromSlot = this.slots.get(slot);
		if (fromSlot.hasStack())
		{
			ItemStack moveItem = fromSlot.getStack();
			itemStack = moveItem.copy();
			if (slot < 3)
			{
				if (!this.insertItem(moveItem, 12, 21, true) && !this.insertItem(moveItem, 21, 48, false))
					return ItemStack.EMPTY;
			}
			else if (slot < 12)
			{
				if (!this.insertItem(moveItem, 0, 2, false) && !this.insertItem(moveItem, 12, 21, true) &&
					!this.insertItem(moveItem, 21, 48, false)) return ItemStack.EMPTY;
			}
			else
			{
				if (!this.insertItem(moveItem, 0, 2, false) && !this.insertItem(moveItem, 3, 12, false))
					return ItemStack.EMPTY;
			}
			if (moveItem.isEmpty()) fromSlot.setStack(ItemStack.EMPTY);
			else fromSlot.markDirty();
			if (moveItem.getCount() == itemStack.getCount())
			{
				return ItemStack.EMPTY;
			}
			fromSlot.onTakeItem(player, moveItem);
		}
		return itemStack;
	}

	private void addPlayerInventorySlots(PlayerInventory playerInventory)
	{
		for (int i = 0; i < 9; i++)
		{
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 178));
		}
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 120 + i * 18));
			}
		}
	}


	private ForgingBlockInventory createInventoryUp()
	{
		ForgingBlockInventory ret = new ForgingBlockInventory(1)
		{
			@Override
			public void onContentChanged()
			{
				if (!clientSide) ForgingBlockScreenHandler.this.onUpDownContentChanged();
				super.onContentChanged();
			}
		};
		this.addSlot(new Slot(ret, 0, 49, 19)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				var item = stack.getItem();
				return item instanceof EnergyToolItem;
			}

			@Override
			public int getMaxItemCount()
			{
				return 1;
			}


		});
		return ret;
	}

	private ForgingBlockInventory createInventoryDown()
	{
		ForgingBlockInventory ret = new ForgingBlockInventory(1)
		{
			@Override
			public void onContentChanged()
			{
				if (!clientSide) ForgingBlockScreenHandler.this.onUpDownContentChanged();
				super.onContentChanged();
			}
		};
		this.addSlot(new Slot(ret, 0, 49, 41)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				var item = stack.getItem();
				return item instanceof UpgradeTemplate || item instanceof AbstractHeart;
			}

			@Override
			public int getMaxItemCount()
			{
				return 1;
			}
		});
		return ret;
	}

	private ForgingBlockInventory createInventoryIngredient()
	{
		ForgingBlockInventory ret = new ForgingBlockInventory(9)
		{
			@Override
			public void onContentChanged()
			{
				if (!clientSide) ForgingBlockScreenHandler.this.onIngredientContentChanged();
				super.onContentChanged();
			}
		};
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(ret, i, 8 + 18 * i, 84));
		return ret;
	}

	private Inventory createInventoryOut()
	{
		Inventory ret = new SimpleInventory(1)
		{
			@Override
			public void markDirty()
			{
				super.markDirty();
			}
		};
		this.addSlot(new Slot(ret, 0, 129, 30)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				return false;
			}

			@Override
			public boolean canTakeItems(PlayerEntity playerEntity)
			{
				return ForgingBlockScreenHandler.this.canTakeOutput(playerEntity);
			}

			@Override
			public void onTakeItem(PlayerEntity playerEntity, ItemStack stack)
			{
				if (!ForgingBlockScreenHandler.this.clientSide)
					ForgingBlockScreenHandler.this.onExtractOutput(playerEntity);
			}
		});
		return ret;
	}

	@Override
	public void onClosed(PlayerEntity player)
	{
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.inventoryUp));
		this.context.run((world, pos) -> this.dropInventory(player, this.inventoryDown));
		this.context.run((world, pos) -> this.dropInventory(player, this.inventoryIngredient));
	}

	//</editor-fold>


	//<editor-fold desc="开放查询">

	public Map<Item, Integer> getUpgradeNeed()
	{
		ItemStack stack = inventoryDown.getStack(0);
		if (stack.getItem() instanceof UpgradeTemplate up)
		{
			var upgrade = up.getUpgrade();
			return upgrade.getIngredients();
		}
		return null;
	}

	@Nullable
	public RepairableComponent getRepairIngredient()
	{
		ItemStack stack = inventoryUp.getStack(0);
		if (stack.getItem() instanceof EnergyToolItem)
		{
			return stack.getOrDefault(DataComponentTypes.REPAIRABLE, DEFUALT_REPAIR_INGREDIENT);
		}
		return null;
	}

	public boolean isOutputInventory(Inventory inventory)
	{
		return inventory == inventoryOut;
	}

	public int getGlobalState()
	{
		return globalState.get();
	}

	public boolean isWorldLevelEnough()
	{
		ItemStack stackDown = inventoryDown.getStack(0);
		Item item = stackDown.getItem();
		if (isEnchantHeart(stackDown))
			return (getWorldLevelOfXpCost(inventoryOut.getStack(0).getOrDefault(IWComponents.ENCHANT_VALUE, -1)) <=
					Server.getPersistentData().worldEnergyLevel);
		if (item instanceof UpgradeTemplate it) return Server.getPersistentData().worldEnergyLevel >= it.getLevel();
		return false;
	}

	public int getRepairCount()
	{
		return repairCount.get();
	}

	public Text getFirstEnchantOut()
	{
		var stackOut = inventoryOut.getStack(0);
		if (stackOut == null) return null;
		ItemEnchantmentsComponent component = stackOut.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS,
				ItemEnchantmentsComponent.DEFAULT);
		if (component.isEmpty()) return null;
		int target = experienceCost.get() >> 10;
		for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<net.minecraft.registry.entry.RegistryEntry<Enchantment>> e : component.getEnchantmentEntries())
		{
			if (target > 1) target--;
			else return IWEnchantmentUtil.getRawName(e.getKey(), e.getIntValue());
		}
		return null;
	}

	//</editor-fold>

	private void updateGlobalState(int current)
	{
		if (globalState.get() != current) globalState.set(current);
	}

	private void removeOutput()
	{
		inventoryOut.removeStack(0);
	}

	private void setOutput(ItemStack stack)
	{
		inventoryOut.setStack(0, stack);
	}

	private boolean isAbilityHeart(ItemStack stack)
	{
		return stack.getItem() instanceof AbstractHeart &&
			   HeartDataFlag.fromItemStack(stack).getTypeTaking() == HeartDataFlag.HeartTypeTaking.ABILITY;
	}

	private boolean isEnchantHeart(ItemStack stack)
	{
		return stack.getItem() instanceof AbstractHeart &&
			   HeartDataFlag.fromItemStack(stack).getTypeTaking() == HeartDataFlag.HeartTypeTaking.ENCHANT;
	}

	private boolean isPreEnchantHeart(ItemStack stack)
	{
		return stack.getItem() instanceof AbstractHeart &&
			   HeartDataFlag.fromItemStack(stack).getTypeTaking() == HeartDataFlag.HeartTypeTaking.PREENCHANT;
	}

	private boolean isEmptyAbilityHeart(ItemStack stack)
	{
		return stack.getItem() instanceof AbstractHeart h && h.supportAbility() &&
			   HeartDataFlag.fromItemStack(stack).getTypeTaking() == HeartDataFlag.HeartTypeTaking.NULL;
	}

	//0: 耐久满 空 -> 标题(所有)
	//>1: 有物品 预附魔 -> 标题(符文附魔), 高亮可能使用的原料格, 输出T附魔符文, 隐藏物品提示, 提示T(输出符文的第一条附魔)
	//>2: 无物品 预附魔 -> 标题(符文附魔), 提示F("放入工具以指示附魔种类")
	//>3: 耐久足够 附魔 -> [如果可以附魔] 标题(附魔), 输出{经验足够&对于符文世界等级足够}T/F附魔工具, 提示T/F(消耗的经验/"你还不能使用该符文")
	//>4: 耐久足够 附魔 -> [如果不能附魔] 标题(附魔), 提示F("没有可用的附魔")
	//>5: 无物品 附魔 -> 标题(附魔), 提示F("放入工具以进行附魔")
	//>6: 耐久不够 附魔,能力,空白,升级 / 耐久足够,耐久不满 空 -> [无修复原料] 标题(修复), 提示F("需要原料以进行修复")
	//>7: 耐久不够 附魔,能力,空白,升级 / 耐久足够,耐久不满 空 -> [有修复原料] 标题(修复), 高亮可能使用的原料格, 输出{经验足够}T/F修复工具, 提示T/F(消耗的经验, 消耗的原料数)
	//>8: 耐久足够,无能力 能力 -> [如果物品匹配] 标题(能力附加), 输出T能力工具
	//>9: 耐久足够,无能力 能力 -> [如果物品不匹配] 标题(能力附加), 提示F("能力和工具不兼容")
	//>10:耐久足够,有能力 能力 -> 标题(能力附加), 提示F("工具已经存在能力")
	//>11:无物品 能力 -> 标题(能力附加), 提示F("放入工具以附加能力")
	//>12:耐久足够,有能力 空白 -> 标题(能力提取), 高亮可能使用的原料格, 输出能力符文, 提示<平滑变化>(工具损坏概率)
	//>13:耐久足够,无能力 空白 -> 标题(能力提取), 提示F("没有可提取的能力")
	//>14:无物品 空白 -> 标题(能力提取), 提示F("放入工具以提取能力")
	//15:耐久足够,无升级 升级 -> [如果原料足够, 物品匹配] 标题(升级), 高亮可能使用的原料格, 输出{世界等级足够}T/F升级工具, 提示T/F(空/"你还不能使用该符文")
	//16:耐久足够,无升级 升级 -> [如果物品不匹配] 标题(升级), 提示F("升级和工具不兼容")
	//17:耐久足够,无升级 升级 -> [如果原料不足, 物品匹配] 标题(升级), 高亮可能使用的原料格, 提示F("缺少原料")
	//18:耐久足够,有升级 升级 -> 标题(升级), 提示F("不能重复升级")
	//19:无物品 升级 -> 标题(升级), 提示F("放入工具以进行升级")
	//21:能力不兼容, 不能提取
	//22: 材料太差, 不能提取
	private void onUpDownContentChanged()
	{
		ItemStack stackUp = inventoryUp.getStack(0);
		ItemStack stackDown = inventoryDown.getStack(0);
		if (stackUp.isEmpty())
		{
			removeOutput();
			if (stackDown.isEmpty())
			{
				updateGlobalState(0);
				return;
			}
			Item itemDown = stackDown.getItem();
			if (itemDown instanceof UpgradeTemplate)
			{
				updateGlobalState(20);
				return;
			}
			AbstractHeart abstractHeart = (AbstractHeart) itemDown;
			HeartDataFlag flagOnlyCheckable = HeartDataFlag.fromItemStack(stackDown);
			var typeTaking = flagOnlyCheckable.getTypeTaking();
			if (typeTaking == HeartDataFlag.HeartTypeTaking.PREENCHANT)
			{
				updateGlobalState(2);
				return;
			}
			if (abstractHeart.supportAbility() && typeTaking == HeartDataFlag.HeartTypeTaking.NULL)
			{
				updateGlobalState(14);
				return;
			}
			if (typeTaking == HeartDataFlag.HeartTypeTaking.ABILITY)
			{
				updateGlobalState(11);
				return;
			}
			if (typeTaking == HeartDataFlag.HeartTypeTaking.ENCHANT)
			{
				updateGlobalState(5);
				return;
			}
			updateGlobalState(0);
			return;
		}
		EnergyToolItem itemUp = (EnergyToolItem) stackUp.getItem();
		if (itemUp == null)
		{
			updateGlobalState(0);
			removeOutput();
			return;
		}

		boolean durabilityEnough = stackUp.getDamage() * 4 <= stackUp.getMaxDamage();
		if (!stackDown.isEmpty())
		{
			Item itemDown = stackDown.getItem();
			AbstractRuneAbility abilityUp = getAbility(stackUp);
			AbstractHeart abstractHeart = itemDown instanceof UpgradeTemplate ? null : (AbstractHeart) itemDown;
			HeartDataFlag flagOnlyCheckable = HeartDataFlag.fromItemStack(stackDown);
			var typeTaking = flagOnlyCheckable.getTypeTaking();
			if (durabilityEnough)
			{
				if (itemDown instanceof UpgradeTemplate upgradeTemplate)
				{
					var flagUp = EnergyToolDataFlag.fromItemStack(stackUp);
					if (flagUp.haveUpgrade())
					{
						updateGlobalState(19);
						removeOutput();
					}
					else
					{
						var upgrade = upgradeTemplate.getUpgrade();
						if (upgrade.canApplyTo(stackUp))
						{
							var ig = upgrade.getIngredients();

							if (ig == null || inventoryHave(upgrade.getIngredients())) updateGlobalState(16);
							else
							{
								updateGlobalState(17);
								removeOutput();
							}
							ItemStack stackOut = stackUp.copy();
							upgrade.applyUpgrade(stackOut);
							setOutput(stackOut);
						}
						else
						{
							updateGlobalState(18);
							removeOutput();
						}
					}
					return;
				}
				if (abstractHeart.supportAbility() && typeTaking == HeartDataFlag.HeartTypeTaking.NULL)
				{
					if (abilityUp == IWRuneAbilities.DEFAULT_ABILITY)
					{
						updateGlobalState(13);
						removeOutput();
					}
					else switch (((AbstractHeart) itemDown).supportAbility(stackDown, abilityUp))
					{
						case SUPPORT ->
						{
							updateGlobalState(12);
							repairCount.set(countToolBreakingProbability(stackUp));
							ItemStack stackOut = inventoryDown.getStack(0).copy();
							((AbstractHeart) stackOut.getItem()).setAbility(stackOut, abilityUp);
							setOutput(stackOut);
						}
						case LOW_LEVEL ->
						{
							updateGlobalState(22);
							removeOutput();
						}
						case NO_ABILITY ->
						{
							updateGlobalState(21);
							removeOutput();
						}
					}
					return;
				}
				if (typeTaking == HeartDataFlag.HeartTypeTaking.ABILITY)
				{
					if (abilityUp != IWRuneAbilities.DEFAULT_ABILITY)
					{
						updateGlobalState(10);
						removeOutput();
					}
					else
					{
						AbstractRuneAbility abilityDown = getAbility(stackDown);
						if (abilityDown.canApplyTo(stackUp))
						{
							updateGlobalState(8);
							ItemStack stackOut = stackUp.copy();
							setAbility(stackOut, abilityDown);
							setOutput(stackOut);
						}
						else
						{
							updateGlobalState(9);
							removeOutput();
						}
					}
					return;
				}
				if (typeTaking == HeartDataFlag.HeartTypeTaking.ENCHANT)
				{
					ItemStack stackOut = stackUp.copy();
					int cost = applyEnchant(stackDown.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS,
							ItemEnchantmentsComponent.DEFAULT), stackOut);
					if (cost > -1)
					{
						experienceCost.set(cost);
						updateGlobalState(3);
						setOutput(stackOut);
					}
					else
					{
						updateGlobalState(4);
						removeOutput();
					}
					return;
				}
			}
			if (abstractHeart != null && typeTaking == HeartDataFlag.HeartTypeTaking.PREENCHANT)
			{
				MutableInt crystal_count = new MutableInt(inventoryCount(Items.AMETHYST_SHARD));
				MutableInt lapis_count = new MutableInt(inventoryCount(Items.LAPIS_LAZULI));
				MutableInt cost_achieve = new MutableInt(0);
				ItemEnchantmentsComponent component = TableEnchantGenerator.generateFromItemStack(stackUp, stackDown,
						random_seed, crystal_count, lapis_count, cost_achieve);
				repairCount.set(lapis_count.getValue());
				experienceCost.set(crystal_count.getValue());
				if (component == null || component.isEmpty())
				{
					updateGlobalState(15);
					removeOutput();
					return;
				}
				updateGlobalState(1);
				ItemStack stackOut = stackDown.copy();
				stackOut.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
				((AbstractHeart) stackDown.getItem()).setEnchant(stackOut, component, cost_achieve.getValue());
				setOutput(stackOut);
				return;
			}
		}
		boolean durabilityFull = stackUp.getDamage() == 0;
		if ((!durabilityEnough) || (!durabilityFull && stackDown.isEmpty()))
		{
			int haveCount = 0;
			repairIngredient = stackUp.getOrDefault(DataComponentTypes.REPAIRABLE, DEFUALT_REPAIR_INGREDIENT);
			if (repairIngredient != null) for (int i = 0; i < 9; i++)
			{
				ItemStack cstack = inventoryIngredient.getStack(i);
				if (repairIngredient.matches(cstack))
				{
					haveCount += cstack.getCount();
					if (haveCount > 3) break;
				}
			}
			if (haveCount > 0)
			{
				updateGlobalState(7);
				int damage = stackUp.getDamage();
				int perRepair = (stackUp.getMaxDamage() + 3) >> 2;
				int rp = Math.min(haveCount, (damage + perRepair - 1) / perRepair);
				repairCount.set(rp);
				int currentDamage = Math.max(0, damage - rp * perRepair);
				ItemStack stackOut = stackUp.copy();
				stackOut.setDamage(currentDamage);
				setOutput(stackOut);
				experienceCost.set(EnergyToolDataFlag.fromItemStack(stackUp).level() * rp * 25);
			}
			else
			{
				updateGlobalState(6);
				removeOutput();
			}
			return;
		}
		updateGlobalState(0);
		removeOutput();
	}

	private void onIngredientContentChanged()
	{
		switch (globalState.get())
		{
			case 6 ->
			{
				ItemStack stackUp = inventoryUp.getStack(0);
				int haveCount = 0;
				for (int i = 0; i < 9; i++)
				{
					ItemStack cstack = inventoryIngredient.getStack(i);
					if (repairIngredient.matches(cstack))
					{
						haveCount += cstack.getCount();
						if (haveCount > 3) break;
					}
				}
				if (haveCount > 0)
				{
					updateGlobalState(7);
					int damage = stackUp.getDamage();
					int perRepair = (stackUp.getMaxDamage() + 3) >> 2;
					int rp = Math.min(haveCount, (damage + perRepair - 1) / perRepair);
					repairCount.set(rp);
					int currentDamage = Math.max(0, damage - rp * perRepair);
					ItemStack stackOut = stackUp.copy();
					stackOut.setDamage(currentDamage);
					setOutput(stackOut);
					experienceCost.set(EnergyToolDataFlag.fromItemStack(stackUp).level() * rp * 25);
				}
			}
			case 7 ->
			{
				ItemStack stackUp = inventoryUp.getStack(0);
				int haveCount = 0;
				for (int i = 0; i < 9; i++)
				{
					ItemStack cstack = inventoryIngredient.getStack(i);
					if (repairIngredient.matches(cstack))
					{
						haveCount += cstack.getCount();
						if (haveCount > 3) break;
					}
				}
				if (haveCount > 0)
				{
					updateGlobalState(7);
					int damage = stackUp.getDamage();
					int perRepair = (stackUp.getMaxDamage() + 3) >> 2;
					int rp = Math.min(haveCount, (damage + perRepair - 1) / perRepair);
					repairCount.set(rp);
					int currentDamage = Math.max(0, damage - rp * perRepair);
					ItemStack stackOut = stackUp.copy();
					stackOut.setDamage(currentDamage);
					setOutput(stackOut);
					experienceCost.set(EnergyToolDataFlag.fromItemStack(stackUp).level() * rp * 25);
				}
				else
				{
					updateGlobalState(6);
					removeOutput();
				}
			}
			case 12 ->
			{
				ItemStack stackUp = inventoryUp.getStack(0);
				if (stackUp.isEmpty()) return;
				int nextCost = countToolBreakingProbability(stackUp);
				if (nextCost != repairCount.get()) repairCount.set(nextCost);
			}
			case 1, 15 ->
			{
				MutableInt crystal_count = new MutableInt(inventoryCount(Items.AMETHYST_SHARD));
				MutableInt lapis_count = new MutableInt(inventoryCount(Items.LAPIS_LAZULI));
				ItemStack stackUp = inventoryUp.getStack(0);
				ItemStack stackDown = inventoryDown.getStack(0);
				MutableInt cost_achieve = new MutableInt(0);
				ItemEnchantmentsComponent component = TableEnchantGenerator.generateFromItemStack(stackUp, stackDown,
						random_seed, crystal_count, lapis_count, cost_achieve);
				repairCount.set(lapis_count.getValue());
				experienceCost.set(crystal_count.getValue());
				if (component == null || component.isEmpty())
				{
					updateGlobalState(15);
					removeOutput();
					return;
				}
				updateGlobalState(1);
				ItemStack stackOut = stackDown.copy();
				((AbstractHeart) stackDown.getItem()).dumpPreEnchant(stackOut, component, cost_achieve.getValue());
				setOutput(stackOut);
			}
			case 16, 17 ->
			{
				ItemStack stackDown = inventoryDown.getStack(0);
				var upgrade = ((UpgradeTemplate) stackDown.getItem()).getUpgrade();
				var ig = upgrade.getIngredients();
				if (ig == null || inventoryHave(upgrade.getIngredients())) updateGlobalState(16);
				else updateGlobalState(17);
			}
		}
	}

	// 元调用不为物品栏状态负责
	private void extractItemForSavingTool(int abilityLevelOfTool)
	{
		if (abilityLevelOfTool == 0) return;
		int need = abilityLevelOfTool * 16;
		int[] valuePerItem = new int[9];
		int[] countOfItem = new int[9];
		int[] hashIndex = new int[9];
		for (int i = 0; i < 9; i++)
		{
			ItemStack s = inventoryIngredient.getStack(i);
			if (!s.isEmpty() && IWResources.RuneItemValue.RuneItemValue.containsKey(s.getItem()))
			{
				valuePerItem[i] = IWResources.RuneItemValue.RuneItemValue.getOrDefault(s.getItem(), 0);
				countOfItem[i] = s.getCount();
			}
			hashIndex[i] = i;
		}
		for (int kj = 1; kj <= 8; kj++)
		{
			int key = hashIndex[kj];
			int ki = kj - 1;
			while (ki >= 0 && valuePerItem[hashIndex[ki]] > valuePerItem[key])
			{
				hashIndex[ki + 1] = hashIndex[ki];
				ki = ki - 1;
			}
			hashIndex[ki + 1] = key;
		}
		for (int i = 0; i < 9; i++)
		{
			int index = hashIndex[i];
			int value = valuePerItem[index];
			if (value == 0) continue;
			int count = countOfItem[index];
			int needCount = (need + value - 1) / value;
			if (count > needCount)
			{
				ItemStack s = inventoryIngredient.getStack(index);
				s.setCount(count - needCount);
				inventoryIngredient.setStack(index, s);
				return;
			}
			else if (count == needCount)
			{
				inventoryIngredient.setStack(index, ItemStack.EMPTY);
				return;
			}
			else
			{
				need -= count * value;
				inventoryIngredient.setStack(index, ItemStack.EMPTY);
			}
		}
	}

	private int countToolBreakingProbability(ItemStack stack)
	{
		int abilityLevelOfTool = getAbility(stack).level();
		float pro = (float) stack.getDamage() / stack.getMaxDamage();
		if (abilityLevelOfTool == 0) return 0;
		int originNeed = abilityLevelOfTool * 16;
		int need = abilityLevelOfTool * 16;
		for (int i = 0; i < 9; i++)
		{
			ItemStack s = inventoryIngredient.getStack(i);
			int value = IWResources.RuneItemValue.RuneItemValue.getOrDefault(s.getItem(), 0);
			if (value == 0) continue;
			int count = s.getCount();
			int needCount = (need + value - 1) / value;
			if (count >= needCount) return Math.clamp((int) pro, 0, 100);
			else need -= count * value;
		}
		return Math.clamp((int) ((float) (need * 100) / originNeed + pro), 0, 100);
	}

	private boolean inventoryHave(Map<Item, Integer> need)
	{
		Map<Item, Integer> current = new HashMap<>(need);
		int size = current.size();
		for (int i = 0; i < 9; i++)
		{
			ItemStack s = inventoryIngredient.getStack(i);
			int n = s.getCount();
			if (n <= 0) continue;
			Item it = s.getItem();
			int c = current.getOrDefault(it, 0);
			if (c > 0)
			{
				if (n < c) current.put(it, c - n);
				else
				{
					current.remove(it);
					size--;
					if (size < 1) return true;
				}
			}
		}
		return false;
	}

	private int inventoryCount(Item it)
	{
		int c = 0;
		for (int i = 0; i < 9; i++)
		{
			ItemStack s = inventoryIngredient.getStack(i);
			if (s.getItem() == it) c += s.getCount();
		}
		return c;
	}

	private void extractIngredient(Item it, int count)
	{
		for (int i = 0; i < 9; i++)
		{
			ItemStack s = inventoryIngredient.getStack(i);
			if (s.getItem() == it)
			{
				int c = s.getCount();
				if (c > count)
				{
					s.decrement(count);
					inventoryIngredient.setStack(i, s);
					return;
				}
				else if (c < count)
				{
					count -= c;
					inventoryIngredient.setStack(i, ItemStack.EMPTY);
				}
				else
				{
					inventoryIngredient.setStack(i, ItemStack.EMPTY);
					return;
				}
			}
		}
	}

	private void extractIngredient(Map<Item, Integer> need)
	{
		Map<Item, Integer> current = new HashMap<>(need);
		for (int i = 0; i < 9; i++)
		{
			ItemStack s = inventoryIngredient.getStack(i);
			int n = s.getCount();
			if (n <= 0) continue;
			Item it = s.getItem();
			int c = current.getOrDefault(it, 0);
			if (c > 0)
			{
				if (n < c)
				{
					current.put(it, c - n);
					inventoryIngredient.removeStack(i);
				}
				else if (n > c)
				{
					current.remove(it);
					s.decrement(c);
					inventoryIngredient.setStack(i, s);
				}
				else
				{
					current.remove(it);
					inventoryIngredient.removeStack(i);
				}
			}
		}
	}

	private void extractIngredient(RepairableComponent predicate, int count)
	{
		for (int i = 0; i < 9; i++)
		{
			ItemStack s = inventoryIngredient.getStack(i);
			if (predicate.matches(s))
			{
				int c = s.getCount();
				if (c > count)
				{
					s.decrement(count);
					inventoryIngredient.setStack(i, s);
					return;
				}
				else if (c < count)
				{
					count -= c;
					inventoryIngredient.setStack(i, ItemStack.EMPTY);
				}
				else
				{
					inventoryIngredient.setStack(i, ItemStack.EMPTY);
					return;
				}
			}
		}
	}

	public void onExtractOutput(PlayerEntity player)
	{
		switch (globalState.get())
		{
			case 1 ->//符文附魔
			{
				inventoryIngredient.setSleeping();
				inventoryDown.setWaiting();
				inventoryDown.setStack(0, ItemStack.EMPTY);
				int cr = experienceCost.get() & 0b01111111111;
				int lp = repairCount.get();
				if (lp > 0) extractIngredient(Items.LAPIS_LAZULI, lp);
				if (cr > 0) extractIngredient(Items.AMETHYST_SHARD, cr);
				random_seed = rand.nextInt();
				inventoryIngredient.setActive();
				inventoryDown.setActive();
			}
			case 3 -> // 附魔
			{
				player.addExperience(-experienceCost.get());
				inventoryUp.setWaiting();
				inventoryDown.setSleeping();
				inventoryUp.setStack(0, ItemStack.EMPTY);
				var stackDown = inventoryDown.getStack(0);
				if (!isEnchantHeart(stackDown)) inventoryDown.removeStack(0);
				else
				{
					((AbstractHeart) stackDown.getItem()).removeEnchant(stackDown);
					inventoryDown.setStack(0, stackDown);
				}
				inventoryDown.setActive();
				inventoryUp.setActive();
			}
			case 7 -> // 修补
			{
				player.addExperience(-experienceCost.get());
				inventoryUp.setWaiting();
				inventoryIngredient.setSleeping();
				extractIngredient(repairIngredient, repairCount.get());
				inventoryUp.setStack(0, ItemStack.EMPTY);
				inventoryIngredient.setActive();
				inventoryUp.setActive();
			}
			case 8 -> //能力附加
			{
				inventoryUp.setWaiting();
				inventoryDown.setSleeping();
				inventoryUp.setStack(0, ItemStack.EMPTY);
				var stackDown = inventoryDown.getStack(0);
				if (isAbilityHeart(stackDown))
				{
					AbstractHeart itemDown = (AbstractHeart) stackDown.getItem();
					itemDown.removeAbility(stackDown);
					inventoryDown.setStack(0, stackDown);
				}
				else inventoryDown.setStack(0, IWItems.COPPER_HEART.getDefaultStack());
				inventoryDown.setActive();
				inventoryUp.setActive();
			}
			case 12 -> //能力提取
			{
				ItemStack stackUp = inventoryUp.getStack(0);
				if (stackUp.isEmpty()) return;
				inventoryUp.setWaiting();
				inventoryDown.setSleeping();
				inventoryIngredient.setSleeping();
				boolean isOk = rand.nextEvenInt(1, 100) > repairCount.get();
				if (isOk)
				{
					extractItemForSavingTool(getAbility(stackUp).level());
					removeToolAbility(stackUp);
					inventoryUp.setStack(0, stackUp);
				}
				else inventoryUp.setStack(0, ItemStack.EMPTY);
				inventoryDown.setStack(0, ItemStack.EMPTY);
				inventoryDown.setActive();
				inventoryIngredient.setActive();
				inventoryUp.setActive();
			}
			case 16 -> // 升级
			{
				inventoryUp.setWaiting();
				inventoryIngredient.setSleeping();
				ItemStack stackDown = inventoryDown.getStack(0);
				var upgrade = ((UpgradeTemplate) stackDown.getItem()).getUpgrade();
				var ig = upgrade.getIngredients();
				if (ig != null) extractIngredient(ig);
				inventoryUp.removeStack(0);
				inventoryIngredient.setActive();
				inventoryUp.setActive();
			}
		}
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return canUse(this.context, player, IWBlocks.FORGING_BLOCK);
	}

	public boolean canTakeOutput(PlayerEntity player)
	{
		switch (globalState.get())
		{
			case 3 ->
			{
				return (getWorldLevelOfXpCost(inventoryOut.getStack(0).getOrDefault(IWComponents.ENCHANT_VALUE, -1)) <=
						Server.getPersistentData().worldEnergyLevel) &&
					   (getExperienceFromLevel(player.experienceLevel, player.experienceProgress) >=
						experienceCost.get());
			}
			case 7 ->
			{
				return getExperienceFromLevel(player.experienceLevel, player.experienceProgress) >=
					   experienceCost.get();
			}
			case 1, 8, 12 ->
			{
				return true;
			}
			case 16 ->
			{
				ItemStack stackDown = inventoryDown.getStack(0);
				if (!(stackDown.getItem() instanceof UpgradeTemplate it)) return false;
				return Server.getPersistentData().worldEnergyLevel >= it.getLevel();
			}
		}
		return false;
	}

	public int getExperienceCost()
	{
		switch (globalState.get())
		{
			case 3, 7 ->
			{
				return experienceCost.get();
			}
		}
		return -1;
	}

}
