package org.yang.iw.boost.function;

import org.yang.iw.api.java.ObjectArrayListSet;

import java.util.function.Consumer;

public class BoostFunctionList
{
	final ObjectArrayListSet dataMap;

	public BoostFunctionList()
	{
		dataMap = new ObjectArrayListSet();
	}

	void add(BoostFunctionList list)
	{
		dataMap.addAll(list.dataMap);
	}

	<T> void add(Class<T> type, T value)
	{
		dataMap.add(type, value);
	}

	public void applyItemDamageFunctions(Consumer<ItemDamageFunction> consumer)
	{
		var l = dataMap.get(ItemDamageFunction.class);
		if (l != null) l.forEach(consumer);
	}

	public void applyAttributeModifierFunctions(Consumer<AttributeModifierFunction> consumer)
	{
		var l = dataMap.get(AttributeModifierFunction.class);
		if (l != null) l.forEach(consumer);
	}

	public void applyTargetDamagedFunctions(Consumer<TargetDamagedFunction> consumer)
	{
		var l = dataMap.get(TargetDamagedFunction.class);
		if (l != null) l.forEach(consumer);
	}

	public void applyEquipmentDropChanceFunctions(Consumer<EquipmentDropChanceFunction> consumer)
	{
		var l = dataMap.get(EquipmentDropChanceFunction.class);
		if (l != null) l.forEach(consumer);
	}

	public void applyPretendEnchantInLootTableFunctions(Consumer<PretendEnchantInLootTableFunction> consumer)
	{
		var l = dataMap.get(PretendEnchantInLootTableFunction.class);
		if (l != null) l.forEach(consumer);
	}

	public void applyLeveledSignalFunctions(Consumer<LeveledSignalFunction> consumer)
	{
		var l = dataMap.get(LeveledSignalFunction.class);
		if (l != null) l.forEach(consumer);
	}

	public void applyModifyDamageFunctions(Consumer<ModifyDamageFunction> consumer)
	{
		var l = dataMap.get(ModifyDamageFunction.class);
		if (l != null) l.forEach(consumer);
	}

	public void applyModifyKnockbackFunctions(Consumer<ModifyKnockbackFunction> consumer)
	{
		var l = dataMap.get(ModifyKnockbackFunction.class);
		if (l != null) l.forEach(consumer);
	}

	public void applyAccelerateEnergyTransferFunctions(Consumer<AccelerateEnergyTransferFunction> consumer)
	{
		var l = dataMap.get(AccelerateEnergyTransferFunction.class);
		if (l != null) l.forEach(consumer);
	}
}
