package org.yang.iw.boost.function;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;

import java.util.EnumMap;
import java.util.function.Consumer;

public class BoostFunctionMap
{

	public static final BoostFunctionMap DEFAULT = new BoostFunctionMap(new EnumMap<>(AttributeModifierSlot.class));
	final EnumMap<AttributeModifierSlot, BoostFunctionList> equipmentSlotBoostFunctionListMap;

	BoostFunctionMap(EnumMap<AttributeModifierSlot, BoostFunctionList> map)
	{
		equipmentSlotBoostFunctionListMap = map;
	}

	public void applyForSlot(EquipmentSlot slot, Consumer<BoostFunctionList> functionConsumer)
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

	public void applyForSlot(AttributeModifierSlot slot, Consumer<BoostFunctionList> functionConsumer)
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

		private BoostFunctionList allocate(AttributeModifierSlot slot)
		{
			var original = map.equipmentSlotBoostFunctionListMap.getOrDefault(slot, null);
			if (original == null)
			{
				original = new BoostFunctionList();
				map.equipmentSlotBoostFunctionListMap.put(slot, original);
			}
			return original;
		}

		public Builder add(AttributeModifierSlot slot, BoostFunctionList list)
		{
			if (not_built)
			{
				var original = allocate(slot);
				original.add(list);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, AttributeModifierFunction func)
		{
			if (not_built && func != null && slot != null)
			{
				var original = allocate(slot);
				original.add(AttributeModifierFunction.class, func);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, TargetDamagedFunction func)
		{
			if (not_built && func != null && slot != null)
			{
				var original = allocate(slot);
				original.add(TargetDamagedFunction.class, func);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, EquipmentDropChanceFunction func)
		{
			if (not_built && func != null && slot != null)
			{
				var original = allocate(slot);
				original.add(EquipmentDropChanceFunction.class, func);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, PretendEnchantInLootTableFunction func)
		{
			if (not_built && func != null && slot != null)
			{
				var original = allocate(slot);
				original.add(PretendEnchantInLootTableFunction.class, func);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, ModifyDamageFunction func)
		{
			if (not_built && func != null && slot != null)
			{
				var original = allocate(slot);
				original.add(ModifyDamageFunction.class, func);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, LeveledSignalFunction func)
		{
			if (not_built && func != null && slot != null)
			{
				var original = allocate(slot);
				original.add(LeveledSignalFunction.class, func);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, ItemDamageFunction func)
		{
			if (not_built && func != null && slot != null)
			{
				var original = allocate(slot);
				original.add(ItemDamageFunction.class, func);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, ModifyKnockbackFunction func)
		{
			if (not_built && func != null && slot != null)
			{
				var original = allocate(slot);
				original.add(ModifyKnockbackFunction.class, func);
			}
			return this;
		}

		public Builder add(AttributeModifierSlot slot, AccelerateEnergyTransferFunction func)
		{
			if (not_built && func != null && slot != null)
			{
				var original = allocate(slot);
				original.add(AccelerateEnergyTransferFunction.class, func);
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
