package org.yang.iw.block.forgingblock;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.RepairableComponent;
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
import org.jetbrains.annotations.Nullable;
import org.yang.iw.IWResources;
import org.yang.iw.IWScreenHandlers;
import org.yang.iw.block.IWBlocks;
import org.yang.iw.boost.pool.TableBoostGenerator;
import org.yang.iw.component.AbilityComponent;
import org.yang.iw.component.BoostComponent;
import org.yang.iw.component.BoostableComponent;
import org.yang.iw.component.IWComponents;
import org.yang.iw.item.IWItems;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.item.heart.BaseHeart;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.item.upgrade.UpgradeTemplate;
import org.yang.iw.util.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.yang.iw.util.IWCostUtil.getExperienceFromLevel;

public class ForgingBlockScreenHandler extends ScreenHandler
{

	private static final RepairableComponent DEFAULT_REPAIR_INGREDIENT = new RepairableComponent(
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

	private final ForgingBlockStateProperty globalState;
	private final Property experienceCost;
	private RepairableComponent repairIngredient;
	private final Property repairCount;

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
			var data = ((ServerPlayerEntity) playerInventory.player).interestingWorld$getIWServerPlayerData();
			this.random_seed = data.forging_seed;
		}
		else this.random_seed = 0;
		this.inventoryUp = createInventoryUp();
		this.inventoryDown = createInventoryDown();
		this.inventoryOut = createInventoryOut();
		this.inventoryIngredient = createInventoryIngredient();
		this.addPlayerInventorySlots(playerInventory);
		globalState = new ForgingBlockStateProperty();
		this.addProperty(globalState.getProperty());
		globalState.set(ForgingBlockState.EMPTY);
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
				return item instanceof UpgradeTemplate || item instanceof AbilityHeart || item instanceof BaseHeart;
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
			return stack.getOrDefault(DataComponentTypes.REPAIRABLE, DEFAULT_REPAIR_INGREDIENT);
		}
		return null;
	}

	public boolean isOutputInventory(Inventory inventory)
	{
		return inventory == inventoryOut;
	}

	public ForgingBlockState getGlobalState()
	{
		return globalState.get();
	}

	public boolean isWorldLevelEnough()
	{
		ItemStack stackDown = inventoryDown.getStack(0);
		Item item = stackDown.getItem();
		if (isBoostHeart(stackDown)) return (inventoryOut.getStack(0).interestingWorld$getBoosts().level() <=
											 Server.getPersistentData().worldEnergyLevel);
		if (item instanceof UpgradeTemplate it) return Server.getPersistentData().worldEnergyLevel >= it.getLevel();
		return false;
	}

	public int getRepairCount()
	{
		return repairCount.get();
	}

	public Text getFirstBoostOut()
	{
		var stackOut = inventoryOut.getStack(0);
		return stackOut.interestingWorld$getBoosts().randomEntryText(experienceCost.get());
	}

	//</editor-fold>

	private void updateGlobalState(ForgingBlockState current)
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
		return stack.getItem() instanceof AbilityHeart && !stack.interestingWorld$getAbility().isEmpty();
	}

	private boolean isBoostHeart(ItemStack stack)
	{
		return stack.getItem() instanceof BaseHeart && !stack.interestingWorld$getBoosts().isEmpty();
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
	//22: 耐久足够 附魔 -> [如果强化不足] 标题(附魔), 提示F("没有可用的附魔")
	private void onUpDownContentChanged()
	{
		ItemStack stackUp = inventoryUp.getStack(0);
		ItemStack stackDown = inventoryDown.getStack(0);
		if (stackDown.isEmpty())
		{
			if (stackUp.isEmpty())
			{
				updateGlobalState(ForgingBlockState.EMPTY);
				removeOutput();
				return;
			}
			int currentDamage = stackUp.getDamage();
			int maxDamage = stackUp.getMaxDamage();
			if (currentDamage <= 0)
			{
				updateGlobalState(ForgingBlockState.EMPTY);
				removeOutput();
				return;
			}
			repairIngredient = stackUp.getOrDefault(DataComponentTypes.REPAIRABLE, DEFAULT_REPAIR_INGREDIENT);
			if (repairIngredient == DEFAULT_REPAIR_INGREDIENT)
			{
				updateGlobalState(ForgingBlockState.REPAIR_DISABLE);
				removeOutput();
				return;
			}
			int repairNeedMax = stackUp.getOrDefault(IWComponents.REPAIR_COST, (short) 1);
			if (repairNeedMax < 1) repairNeedMax = 1;
			int haveCount = 0;
			for (int i = 0; i < 9; i++)
			{
				ItemStack stack = inventoryIngredient.getStack(i);
				if (repairIngredient.matches(stack))
				{
					haveCount += stack.getCount();
					if (haveCount >= repairNeedMax) break;
				}
			}
			if (haveCount > 0)
			{
				updateGlobalState(ForgingBlockState.REPAIR);
				int perRepair = (maxDamage + repairNeedMax - 1) / repairNeedMax;
				int rp = Math.min(haveCount, (currentDamage + perRepair - 1) / perRepair);
				repairCount.set(rp);
				int repairedDamage = Math.max(0, currentDamage - rp * perRepair);
				ItemStack stackOut = stackUp.copy();
				stackOut.setDamage(repairedDamage);
				setOutput(stackOut);
				experienceCost.set((stackUp.interestingWorld$getUpgrade().upgrade().level() +
									stackUp.interestingWorld$getBoosts().level() +
									stackUp.interestingWorld$getAbility().ability().level()) * 100 * currentDamage /
								   maxDamage);
			}
			else
			{
				updateGlobalState(ForgingBlockState.REPAIR_LACK_INGREDIENT);
				removeOutput();
			}
			return;
		}
		Item itemDown = stackDown.getItem();
		if (itemDown instanceof BaseHeart baseHeart)
		{
			boolean noBoost = stackDown.interestingWorld$getBoosts().isEmpty();
			if (noBoost)
			{
				if (stackUp.isEmpty())
				{
					updateGlobalState(ForgingBlockState.APPEND_BOOST_NEED_TOOL_HINT);
					removeOutput();
				}
				else
				{
					EnergyToolItem itemUp = (EnergyToolItem) stackUp.getItem();
					BoostableComponent boostableComponent = stackDown.getOrDefault(IWComponents.BOOSTABLE,
							BoostableComponent.DEFAULT);
					if (boostableComponent.remainBoostTime() < 1)
					{
						updateGlobalState(ForgingBlockState.APPEND_BOOST_NO_BOOST_TIME);
						removeOutput();
						return;
					}
					TableBoostGenerator generator = new TableBoostGenerator(itemUp.getTableBoostPool(),
							stackDown.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT),
							boostableComponent.remainBoostTime(), inventoryCount(Items.LAPIS_LAZULI),
							Server.getPersistentData().worldEnergyLevel, baseHeart.materialLevel, random_seed);
					BoostComponent component = generator.getBoostComponent();
					if (component.isEmpty())
					{
						updateGlobalState(ForgingBlockState.APPEND_BOOST_NO_AVAILABLE);
						removeOutput();
						return;
					}
					repairCount.set(generator.getLazuriteCost());
					updateGlobalState(ForgingBlockState.APPEND_BOOST);
					ItemStack stackOut = stackDown.copy();
					stackOut.set(IWComponents.BOOST, component);
					stackOut.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
					if (!boostableComponent.isEmpty()) stackOut.set(IWComponents.BOOSTABLE,
							boostableComponent.hardUse(generator.getBoostTimeConsume()));
					setOutput(stackOut);
				}
			}
			else
			{
				if (stackUp.isEmpty())
				{
					updateGlobalState(ForgingBlockState.BOOST_NEED_TOOL);
					removeOutput();
				}
				else
				{
					ItemStack stackOut = stackUp.copy();
					BoostComponent boostComponent = stackDown.interestingWorld$getBoosts();
					int cost = boostComponent.applyTo(stackOut);
					if (cost > 0)
					{
						experienceCost.set(cost);
						updateGlobalState(ForgingBlockState.BOOST);
						setOutput(stackOut);
					}
					else if (cost == 0)
					{
						updateGlobalState(ForgingBlockState.BOOST_NO_SUITABLE);
						removeOutput();
					}
					else
					{
						updateGlobalState(ForgingBlockState.BOOST_NO_SLOT);
						removeOutput();
					}
				}
			}
			return;
		}
		if (itemDown instanceof AbilityHeart abilityHeart)
		{
			var abilityDown = stackDown.interestingWorld$getAbility().ability();
			if (abilityDown.isEmpty())
			{
				if (stackUp.isEmpty())
				{
					updateGlobalState(ForgingBlockState.EXTRACT_ABILITY_NO_TOOL);
					removeOutput();
				}
				else
				{
					var abilityUp = stackUp.interestingWorld$getAbility().ability();
					if (abilityUp.isEmpty())
					{
						updateGlobalState(ForgingBlockState.EXTRACT_ABILITY_NO_ABILITY);
						removeOutput();
					}
					else if (abilityHeart.supportAbility(abilityUp))
					{
						updateGlobalState(ForgingBlockState.EXTRACT_ABILITY);
						ItemStack stackOut = stackDown.copy();
						((AbilityHeart) stackOut.getItem()).setAbility(stackOut, abilityUp);
						setOutput(stackOut);
					}
					else
					{
						updateGlobalState(ForgingBlockState.EXTRACT_ABILITY_NOT_SUPPORT);
						removeOutput();
					}
				}
			}
			else
			{
				if (stackUp.isEmpty())
				{
					updateGlobalState(ForgingBlockState.APPEND_ABILITY_NO_TOOL);
					removeOutput();
				}
				else
				{
					if (stackUp.interestingWorld$getAbility().ability().isEmpty())
					{
						if (abilityDown.canApplyTo(stackUp))
						{
							updateGlobalState(ForgingBlockState.APPEND_ABILITY);
							ItemStack stackOut = stackUp.copy();
							stackOut.set(IWComponents.ABILITY, new AbilityComponent(abilityDown));
							setOutput(stackOut);
						}
						else
						{
							updateGlobalState(ForgingBlockState.APPEND_ABILITY_NOT_SUITABLE);
							removeOutput();
						}
					}
					else
					{
						updateGlobalState(ForgingBlockState.APPEND_ABILITY_ALREADY_HAVE);
						removeOutput();
					}
				}
			}
			return;
		}
		if (itemDown instanceof UpgradeTemplate upgradeTemplate)
		{
			if (stackUp.isEmpty())
			{
				updateGlobalState(ForgingBlockState.UPGRADE_NEED_TOOL);
				removeOutput();
			}
			else
			{
				if (stackUp.interestingWorld$getUpgrade().isEmpty())
				{
					var upgradeDown = upgradeTemplate.getUpgrade();
					if (upgradeDown.canApplyTo(stackUp))
					{
						if (upgradeDown.level() > Server.getPersistentData().worldEnergyLevel)
							updateGlobalState(ForgingBlockState.UPGRADE_LEVEL_LOW);
						else
						{
							var ingredients = upgradeDown.getIngredients();
							if (ingredients == null || inventoryHave(upgradeDown.getIngredients()))
								updateGlobalState(ForgingBlockState.UPGRADE);
							else updateGlobalState(ForgingBlockState.UPGRADE_LACK_INGREDIENTS);
						}
						ItemStack stackOut = stackUp.copy();
						upgradeDown.applyUpgrade(stackOut);
						setOutput(stackOut);
					}
					else
					{
						updateGlobalState(ForgingBlockState.UPGRADE_NOT_SUITABLE);
						removeOutput();
					}
				}
				else
				{
					updateGlobalState(ForgingBlockState.UPGRADE_ALREADY_HAVE);
					removeOutput();
				}
			}
			return;
		}
		updateGlobalState(ForgingBlockState.EMPTY);
		removeOutput();
	}

	private void onIngredientContentChanged()
	{
		switch (globalState.get())
		{
			case REPAIR, REPAIR_LACK_INGREDIENT ->
			{
				ItemStack stackUp = inventoryUp.getStack(0);
				int haveCount = 0;
				int repairNeedMax = stackUp.getOrDefault(IWComponents.REPAIR_COST, (short) 1);
				if (repairNeedMax < 1) repairNeedMax = 1;
				for (int i = 0; i < 9; i++)
				{
					ItemStack stack = inventoryIngredient.getStack(i);
					if (repairIngredient.matches(stack))
					{
						haveCount += stack.getCount();
						if (haveCount >= repairNeedMax) break;
					}
				}
				if (haveCount > 0)
				{
					updateGlobalState(ForgingBlockState.REPAIR);
					int damage = stackUp.getDamage();
					int maxDamage = stackUp.getMaxDamage();
					int perRepair = (maxDamage + repairNeedMax - 1) / repairNeedMax;
					int rp = Math.min(haveCount, (damage + perRepair - 1) / perRepair);
					repairCount.set(rp);
					int currentDamage = Math.max(0, damage - rp * perRepair);
					ItemStack stackOut = stackUp.copy();
					stackOut.setDamage(currentDamage);
					setOutput(stackOut);
					experienceCost.set((stackUp.interestingWorld$getUpgrade().upgrade().level() +
										stackUp.interestingWorld$getBoosts().level() +
										stackUp.interestingWorld$getAbility().ability().level()) * 100 * damage /
									   maxDamage);
				}
				else
				{
					updateGlobalState(ForgingBlockState.REPAIR_LACK_INGREDIENT);
					removeOutput();
				}
			}
			case APPEND_BOOST_NO_AVAILABLE, APPEND_BOOST ->
			{
				ItemStack stackDown = inventoryDown.getStack(0);
				ItemStack stackUp = inventoryUp.getStack(0);
				EnergyToolItem itemUp = (EnergyToolItem) stackUp.getItem();
				BaseHeart baseHeart = (BaseHeart) stackDown.getItem();
				BoostableComponent boostableComponent = stackDown.getOrDefault(IWComponents.BOOSTABLE,
						BoostableComponent.DEFAULT);
				if (boostableComponent.remainBoostTime() < 1)
				{
					updateGlobalState(ForgingBlockState.APPEND_BOOST_NO_BOOST_TIME);
					removeOutput();
					return;
				}
				TableBoostGenerator generator = new TableBoostGenerator(itemUp.getTableBoostPool(),
						stackDown.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT),
						boostableComponent.remainBoostTime(), inventoryCount(Items.LAPIS_LAZULI),
						Server.getPersistentData().worldEnergyLevel, baseHeart.materialLevel, random_seed);
				BoostComponent component = generator.getBoostComponent();
				repairCount.set(generator.getLazuriteCost());
				if (component.isEmpty())
				{
					updateGlobalState(ForgingBlockState.APPEND_BOOST_NO_AVAILABLE);
					removeOutput();
					return;
				}
				updateGlobalState(ForgingBlockState.APPEND_BOOST);
				ItemStack stackOut = stackDown.copy();
				stackOut.set(IWComponents.BOOST, component);
				stackOut.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
				if (!boostableComponent.isEmpty())
					stackOut.set(IWComponents.BOOSTABLE, boostableComponent.hardUse(generator.getBoostTimeConsume()));
				setOutput(stackOut);
			}
			case UPGRADE_LACK_INGREDIENTS, UPGRADE ->
			{
				ItemStack stackDown = inventoryDown.getStack(0);
				var upgradeDown = ((UpgradeTemplate) stackDown.getItem()).getUpgrade();
				var ingredients = upgradeDown.getIngredients();
				if (ingredients == null || inventoryHave(upgradeDown.getIngredients()))
					updateGlobalState(ForgingBlockState.UPGRADE);
				else updateGlobalState(ForgingBlockState.UPGRADE_LACK_INGREDIENTS);
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
			case REPAIR ->
			{
				player.addExperience(-experienceCost.get());
				inventoryUp.setWaiting();
				inventoryIngredient.setSleeping();
				extractIngredient(repairIngredient, repairCount.get());
				inventoryUp.setStack(0, ItemStack.EMPTY);
				inventoryIngredient.setActive();
				inventoryUp.setActive();
			}
			case APPEND_BOOST ->
			{
				inventoryIngredient.setSleeping();
				inventoryDown.setWaiting();
				inventoryDown.setStack(0, ItemStack.EMPTY);
				int lp = repairCount.get();
				if (lp > 0) extractIngredient(Items.LAPIS_LAZULI, lp);
				Random random = new Random(random_seed);
				random_seed = random.nextInt();
				if (player instanceof ServerPlayerEntity serverPlayerEntity)
					serverPlayerEntity.interestingWorld$getIWServerPlayerData().forging_seed = random_seed;
				inventoryIngredient.setActive();
				inventoryDown.setActive();
			}
			case BOOST ->
			{
				player.addExperience(-experienceCost.get());
				inventoryUp.setWaiting();
				inventoryUp.setStack(0, ItemStack.EMPTY);
				inventoryUp.setActive();
			}
			case APPEND_ABILITY ->
			{
				inventoryUp.setWaiting();
				inventoryDown.setSleeping();
				inventoryUp.setStack(0, ItemStack.EMPTY);
				var stackDown = inventoryDown.getStack(0);
				if (isAbilityHeart(stackDown))
				{
					AbilityHeart itemDown = (AbilityHeart) stackDown.getItem();
					itemDown.removeAbility(stackDown);
					inventoryDown.setStack(0, stackDown);
				}
				else inventoryDown.setStack(0, IWItems.COPPER_HEART.getDefaultStack());
				inventoryDown.setActive();
				inventoryUp.setActive();
			}
			case EXTRACT_ABILITY ->
			{
				ItemStack stackUp = inventoryUp.getStack(0);
				if (stackUp.isEmpty()) return;
				inventoryUp.setWaiting();
				inventoryDown.setSleeping();
				stackUp.remove(IWComponents.ABILITY);
				inventoryUp.setStack(0, stackUp);
				inventoryDown.setStack(0, ItemStack.EMPTY);
				inventoryDown.setActive();
				inventoryUp.setActive();
			}
			case UPGRADE ->
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
			case BOOST ->
			{
				return (inventoryOut.getStack(0).interestingWorld$getBoosts().level() <=
						Server.getPersistentData().worldEnergyLevel) &&
					   (getExperienceFromLevel(player.experienceLevel, player.experienceProgress) >=
						experienceCost.get());
			}
			case REPAIR ->
			{
				return getExperienceFromLevel(player.experienceLevel, player.experienceProgress) >=
					   experienceCost.get();
			}
			case APPEND_BOOST, APPEND_ABILITY, EXTRACT_ABILITY ->
			{
				return true;
			}
			case UPGRADE ->
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
			case BOOST, REPAIR ->
			{
				return experienceCost.get();
			}
		}
		return -1;
	}

}
