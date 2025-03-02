package org.yang.iw.boost.function;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;

import java.util.EnumMap;
import java.util.function.Consumer;

public class BoostFunctionMap
{

	public static final BoostFunctionMap DEFAULT = new BoostFunctionMap(new EnumMap<>(AttributeModifierSlot.class));
	EnumMap<AttributeModifierSlot, PositionedBoostFunctionList> equipmentSlotBoostFunctionListMap;

	BoostFunctionMap(EnumMap<AttributeModifierSlot, PositionedBoostFunctionList> map)
	{
		equipmentSlotBoostFunctionListMap = map;
	}

	public void applyForSlot(EquipmentSlot slot, Consumer<PositionedBoostFunctionList> functionConsumer)
	{
		applyForSlot(AttributeModifierSlot.ANY, functionConsumer);
		switch (slot)
		{
			case MAINHAND ->
			{
				applyForSlot(AttributeModifierSlot.HAND, functionConsumer);
				applyForSlot(AttributeModifierSlot.MAINHAND, functionConsumer);
			}
			case OFFHAND ->
			{
				applyForSlot(AttributeModifierSlot.HAND, functionConsumer);
				applyForSlot(AttributeModifierSlot.OFFHAND, functionConsumer);
			}
			case FEET ->
			{
				applyForSlot(AttributeModifierSlot.ARMOR, functionConsumer);
				applyForSlot(AttributeModifierSlot.FEET, functionConsumer);
			}
			case LEGS ->
			{
				applyForSlot(AttributeModifierSlot.ARMOR, functionConsumer);
				applyForSlot(AttributeModifierSlot.LEGS, functionConsumer);
			}
			case CHEST ->
			{
				applyForSlot(AttributeModifierSlot.ARMOR, functionConsumer);
				applyForSlot(AttributeModifierSlot.CHEST, functionConsumer);
			}
			case HEAD ->
			{
				applyForSlot(AttributeModifierSlot.ARMOR, functionConsumer);
				applyForSlot(AttributeModifierSlot.HEAD, functionConsumer);
			}
			case BODY ->
			{
				applyForSlot(AttributeModifierSlot.ARMOR, functionConsumer);
				applyForSlot(AttributeModifierSlot.BODY, functionConsumer);
			}
		}
	}

	public void applyForSlot(AttributeModifierSlot slot, Consumer<PositionedBoostFunctionList> functionConsumer)
	{
		equipmentSlotBoostFunctionListMap.forEach((k, v) -> {
			if (slot == k) functionConsumer.accept(v);
		});
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder
	{
		boolean not_built = true;
		BoostFunctionMap map = new BoostFunctionMap(new EnumMap<>(AttributeModifierSlot.class));

		private PositionedBoostFunctionList allocate(AttributeModifierSlot slot)
		{
			var original = map.equipmentSlotBoostFunctionListMap.getOrDefault(slot, null);
			if (original == null)
			{
				original = new PositionedBoostFunctionList();
				map.equipmentSlotBoostFunctionListMap.put(slot, original);
			}
			return original;
		}

		public Builder add(AttributeModifierSlot slot, PositionedBoostFunctionList list)
		{
			if (not_built)
			{
				var original = allocate(slot);
				original.add(list);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, AttributeModifierFunction list)
		{
			if (not_built)
			{
				var original = allocate(slot);
				if (original.attributeModifierFunctions == null)
					original.attributeModifierFunctions = new ObjectArrayList<>();
				original.attributeModifierFunctions.add(list);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, TargetDamagedFunction list)
		{
			if (not_built)
			{
				var original = allocate(slot);
				if (original.targetDamagedFunctions == null) original.targetDamagedFunctions = new ObjectArrayList<>();
				original.targetDamagedFunctions.add(list);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, EquipmentDropChanceFunction list)
		{
			if (not_built)
			{
				var original = allocate(slot);
				if (original.equipmentDropChanceFunctions == null)
					original.equipmentDropChanceFunctions = new ObjectArrayList<>();
				original.equipmentDropChanceFunctions.add(list);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, PretendEnchantInLootTableFunction list)
		{
			if (not_built)
			{
				var original = allocate(slot);
				if (original.pretendEnchantInLootTableFunctions == null)
					original.pretendEnchantInLootTableFunctions = new ObjectArrayList<>();
				original.pretendEnchantInLootTableFunctions.add(list);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, LeveledSignalFunction list)
		{
			if (not_built)
			{
				var original = allocate(slot);
				if (original.leveledSignalFunctions == null) original.leveledSignalFunctions = new ObjectArrayList<>();
				original.leveledSignalFunctions.add(list);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, ItemDamageFunction list)
		{
			if (not_built)
			{
				var original = allocate(slot);
				if (original.itemDamageFunctions == null) original.itemDamageFunctions = new ObjectArrayList<>();
				original.itemDamageFunctions.add(list);
			}
			return this;
		}

		public Builder add(BoostFunctionMap map2Add)
		{
			if (not_built)
			{
				map2Add.equipmentSlotBoostFunctionListMap.forEach((s, l) -> {
					var original = allocate(s);
					original.add(l);
				});
			}
			return this;
		}

		public BoostFunctionMap build()
		{
			not_built = false;
			return map;
		}
	}
}
