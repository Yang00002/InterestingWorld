package org.yang.iw.boost.function;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.function.Consumer;

public class PositionedBoostFunctionList
{
	ObjectArrayList<AttributeModifierFunction> attributeModifierFunctions = null;
	ObjectArrayList<TargetDamagedFunction> targetDamagedFunctions = null;
	ObjectArrayList<EquipmentDropChanceFunction> equipmentDropChanceFunctions = null;
	ObjectArrayList<PretendEnchantInLootTableFunction> pretendEnchantInLootTableFunctions = null;
	ObjectArrayList<LeveledSignalFunction> leveledSignalFunctions = null;
	ObjectArrayList<ItemDamageFunction> itemDamageFunctions = null;

	void add(PositionedBoostFunctionList list)
	{
		if (attributeModifierFunctions == null) attributeModifierFunctions = list.attributeModifierFunctions;
		else if (list.attributeModifierFunctions != null)
		{
			attributeModifierFunctions.addAll(list.attributeModifierFunctions);
		}
		if (targetDamagedFunctions == null) targetDamagedFunctions = list.targetDamagedFunctions;
		else if (list.targetDamagedFunctions != null)
		{
			targetDamagedFunctions.addAll(list.targetDamagedFunctions);
		}
		if (equipmentDropChanceFunctions == null) equipmentDropChanceFunctions = list.equipmentDropChanceFunctions;
		else if (list.equipmentDropChanceFunctions != null)
		{
			equipmentDropChanceFunctions.addAll(list.equipmentDropChanceFunctions);
		}
		if (pretendEnchantInLootTableFunctions == null)
			pretendEnchantInLootTableFunctions = list.pretendEnchantInLootTableFunctions;
		else if (list.pretendEnchantInLootTableFunctions != null)
		{
			pretendEnchantInLootTableFunctions.addAll(list.pretendEnchantInLootTableFunctions);
		}
		if (leveledSignalFunctions == null) leveledSignalFunctions = list.leveledSignalFunctions;
		else if (list.leveledSignalFunctions != null)
		{
			leveledSignalFunctions.addAll(list.leveledSignalFunctions);
		}
		if (itemDamageFunctions == null) itemDamageFunctions = list.itemDamageFunctions;
		else if (list.itemDamageFunctions != null)
		{
			itemDamageFunctions.addAll(list.itemDamageFunctions);
		}
	}

	public void applyItemDamageFunctions(Consumer<ItemDamageFunction> consumer)
	{
		if (itemDamageFunctions != null) itemDamageFunctions.forEach(consumer);
	}

	public void applyAttributeModifierFunctions(Consumer<AttributeModifierFunction> consumer)
	{
		if (attributeModifierFunctions != null) attributeModifierFunctions.forEach(consumer);
	}


	public void applyTargetDamagedFunctions(Consumer<TargetDamagedFunction> consumer)
	{
		if (targetDamagedFunctions != null) targetDamagedFunctions.forEach(consumer);
	}


	public void applyEquipmentDropChanceFunctions(Consumer<EquipmentDropChanceFunction> consumer)
	{
		if (equipmentDropChanceFunctions != null) equipmentDropChanceFunctions.forEach(consumer);
	}


	public void applyPretendEnchantInLootTableFunctions(Consumer<PretendEnchantInLootTableFunction> consumer)
	{
		if (pretendEnchantInLootTableFunctions != null) pretendEnchantInLootTableFunctions.forEach(consumer);
	}

	public void applyLeveledSignalFunctions(Consumer<LeveledSignalFunction> consumer)
	{
		if (leveledSignalFunctions != null) leveledSignalFunctions.forEach(consumer);
	}
}
