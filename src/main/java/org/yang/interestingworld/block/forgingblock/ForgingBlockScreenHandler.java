package org.yang.interestingworld.block.forgingblock;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import org.yang.interestingworld.*;
import org.yang.interestingworld.item.rune.AbilityRuneItem;
import org.yang.interestingworld.item.rune.EnchantmentRuneItem;
import org.yang.interestingworld.item.rune.RuneItem;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;

import static org.yang.interestingworld.util.RuneAbility.*;
import static org.yang.interestingworld.util.RuneEnchantment.*;

public class ForgingBlockScreenHandler extends ScreenHandler
{
	private final ScreenHandlerContext context;
	private final PlayerEntity player;
	private final Inventory inventory;
	private static final int RESULT_SLOT_INDEX = 2;
	private static final int PLAYER_INVENTORY_START_SLOT_INDEX = 12;
	private static final int PLAYER_HOTBAR_END_SLOT_INDEX = 48;
	private final Property canTakeOutput;
	private final Property experienceCost;

	private static boolean isInIngredientSlot(int slot)
	{
		return slot < RESULT_SLOT_INDEX || (slot > RESULT_SLOT_INDEX && slot < PLAYER_INVENTORY_START_SLOT_INDEX);
	}

	@Override
	public void onClosed(PlayerEntity player)
	{
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
	}

	private float extractItemForSavingTool(int abilityLevelOfTool)
	{
		if (abilityLevelOfTool == 0) return 0.0f;
		int originNeed = abilityLevelOfTool * 16;
		int need = abilityLevelOfTool * 16;
		int[] valuePerItem = new int[9];
		int[] countOfItem = new int[9];
		int[] hashIndex = new int[9];
		for (int i = 0; i < 9; i++)
		{
			ItemStack s = inventory.getStack(i + 3);
			if (!s.isEmpty() && IWResources.RuneItemValue.RuneItemValue.containsKey(s.getItem()))
			{
				valuePerItem[i] = IWResources.RuneItemValue.RuneItemValue.get(s.getItem());
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
				ItemStack s = inventory.getStack(index + 3);
				s.setCount(count - needCount);
				inventory.setStack(index + 3, s);
				return 0.0f;
			}
			else if (count == needCount)
			{
				inventory.removeStack(index + 3);
				return 0.0f;
			}
			else
			{
				need -= count * value;
				inventory.removeStack(index + 3);
			}
		}
		return -1.0f * need / originNeed;
	}

	private Inventory createInventory()
	{
		Inventory ret = new SimpleInventory(12)
		{
			@Override
			public void markDirty()
			{
				super.markDirty();
				ForgingBlockScreenHandler.this.onContentChanged(this);
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
		this.addSlot(new Slot(ret, 1, 49, 41)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				var item = stack.getItem();
				return item instanceof RuneItem;
			}

			@Override
			public int getMaxItemCount()
			{
				return 1;
			}
		});
		this.addSlot(new Slot(ret, 2, 129, 30)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				return false;
			}
		});
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(ret, i + 3, 8 + 18 * i, 84));
		return ret;
	}

	@Override
	public void onContentChanged(Inventory inventory)
	{
		super.onContentChanged(inventory);
		ItemStack stackTool = inventory.getStack(0);
		if (stackTool.isEmpty())
		{
			canTakeOutput.set(0);
			return;
		}
		ItemStack stackRune = inventory.getStack(1);
		if (stackRune.isEmpty())
		{
			canTakeOutput.set(0);
			return;
		}
		ItemStack stackOutput = inventory.getStack(2);
		if (!stackOutput.isEmpty())
		{
			canTakeOutput.set(0);
			return;
		}
		if (!(stackTool.getItem() instanceof EnergyToolItem))
		{
			canTakeOutput.set(0);
			return;
		}
		if (stackTool.getMaxDamage() * 0.1 < stackTool.getDamage())
		{
			canTakeOutput.set(0);
			return;
		}
		if (stackRune.getItem() instanceof AbilityRuneItem)
		{
			if (getAbility(stackTool).index != 0)
			{
				canTakeOutput.set(0);
				return;
			}
			IWAbstractRuneAbility ability = getAbility(stackRune);
			if (ability.canApplyTo(stackTool))
			{
				canTakeOutput.set(1);
				return;
			}
			canTakeOutput.set(0);
			return;
		}
		if (stackRune.getItem() == IWItems.EMPTY_RUNE)
		{
			if (haveRealEnchantment(stackTool))
			{
				canTakeOutput.set(1);
				return;
			}
			IWAbstractRuneAbility ability = getAbility(stackTool);
			if (ability.index != 0)
			{
				canTakeOutput.set(1);
				return;
			}
		}
		if (stackRune.getItem() == IWItems.ENCHANTMENT_RUNE)
		{
			EnchantmentRuneItem it = (EnchantmentRuneItem) stackRune.getItem();
			if (it.getLevel(stackRune) > IWUtil.Server.getPersistentData().worldEnergyLevel)
			{
				canTakeOutput.set(0);
				return;
			}
			if (stackRune.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
					.isEmpty())
			{
				canTakeOutput.set(0);
				return;
			}
			int cost = canEnchantTo(
					stackRune.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT),
					stackTool, player.getWorld());
			if (cost != -1)
			{
				experienceCost.set(cost);
				canTakeOutput.set(2);
				return;
			}
		}
		canTakeOutput.set(0);
	}

	private void addPlayerInventorySlots(PlayerInventory playerInventory)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 120 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++)
		{
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 178));
		}
	}

	public ForgingBlockScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context)
	{
		super(IWScreenHandlers.FORGINGBLOCK_SCREEN_HANDLER, syncId);
		this.context = context;
		this.player = playerInventory.player;
		this.inventory = createInventory();
		this.addPlayerInventorySlots(playerInventory);
		canTakeOutput = Property.create();
		this.addProperty(canTakeOutput);
		canTakeOutput.set(0);
		experienceCost = Property.create();
		this.addProperty(experienceCost);
		experienceCost.set(0);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id)
	{
		if (player == this.player && id == 0)
		{
			generateOutput();
		}
		return true;
	}

	public void generateOutput()
	{
		ItemStack stackTool = inventory.getStack(0);
		if (stackTool.isEmpty()) return;
		ItemStack stackRune = inventory.getStack(1);
		if (stackRune.isEmpty()) return;
		ItemStack stackOutput = inventory.getStack(2);
		if (!stackOutput.isEmpty()) return;
		if (!(stackTool.getItem() instanceof EnergyToolItem)) return;
		float currentDamage = stackTool.getDamage();
		float maxDamage = stackTool.getMaxDamage();
		float damageRatio = currentDamage / maxDamage;
		if (damageRatio >= 0.11) return;
		if (stackRune.getItem() instanceof AbilityRuneItem)
		{
			if (getAbility(stackTool).index != 0) return;
			IWAbstractRuneAbility ability = getAbility(stackRune);
			if (ability.canApplyTo(stackTool))
			{
				setAbility(stackTool, ability);
				inventory.setStack(2, stackTool);
				inventory.removeStack(0);
				inventory.setStack(1, IWItems.EMPTY_RUNE.getDefaultStack());
				return;
			}
			return;
		}
		if (stackRune.getItem() instanceof EnchantmentRuneItem enchantmentRuneItem)
		{
			if (player.totalExperience < experienceCost.get()) return;
			if (enchantmentRuneItem.getLevel(stackRune) > IWUtil.Server.getPersistentData().worldEnergyLevel) return;
			if (stackRune.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
					.isEmpty()) return;
			applyEnchant(
					stackRune.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT),
					stackTool, player.getWorld());
			player.addExperience(-experienceCost.get());
			experienceCost.set(0);
			inventory.setStack(2, stackTool);
			inventory.removeStack(0);
			inventory.setStack(1, IWItems.EMPTY_RUNE.getDefaultStack());
			return;
		}
		if (stackRune.getItem() == IWItems.EMPTY_RUNE)
		{
			if (haveRealEnchantment(stackTool)) return;
			IWAbstractRuneAbility ability = getAbility(stackTool);
			if (ability.index == 0) return;
			ItemStack newRune = IWItems.COMMON_ABILITY_RUNE.getDefaultStack();
			setAbility(newRune, ability);
			inventory.setStack(2, newRune);
			inventory.removeStack(1);
			float c = player.getRandom().nextFloat() + extractItemForSavingTool(ability.level()) - damageRatio;
			if (c < 0.0)
			{
				inventory.removeStack(0);
				return;
			}
			removeToolAbility(stackTool);
			stackTool.setDamage((int) (maxDamage * (1 - c)));
			inventory.setStack(0, stackTool);
		}
	}

	public ForgingBlockScreenHandler(int syncId, PlayerInventory inventory)
	{
		this(syncId, inventory, ScreenHandlerContext.EMPTY);
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return canUse(this.context, player, IWBlocks.FORGING_BLOCK);
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
			if (slot == RESULT_SLOT_INDEX)
			{
				if (!this.insertItem(moveItem, PLAYER_INVENTORY_START_SLOT_INDEX, PLAYER_HOTBAR_END_SLOT_INDEX, true))
				{
					return ItemStack.EMPTY;
				}
				fromSlot.onQuickTransfer(moveItem, itemStack);
			}
			else if (isInIngredientSlot(slot))
			{
				if (!this.insertItem(moveItem, PLAYER_INVENTORY_START_SLOT_INDEX, PLAYER_HOTBAR_END_SLOT_INDEX, false))
				{
					return ItemStack.EMPTY;
				}
			}
			if (moveItem.isEmpty())
			{
				fromSlot.setStack(ItemStack.EMPTY);
			}
			else
			{
				fromSlot.markDirty();
			}

			if (moveItem.getCount() == itemStack.getCount())
			{
				return ItemStack.EMPTY;
			}

			fromSlot.onTakeItem(player, moveItem);
		}

		return itemStack;
	}

	public boolean canTakeOutput()
	{
		int cd = canTakeOutput.get();
		switch (cd)
		{
			case 1 ->
			{
				return true;
			}
			case 2 ->
			{
				return player.totalExperience >= experienceCost.get();
			}
		}
		return false;
	}

	public int getExperienceCost()
	{
		if (canTakeOutput.get() == 2) return experienceCost.get();
		else return -1;
	}

}
