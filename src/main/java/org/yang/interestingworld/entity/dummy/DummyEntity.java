package org.yang.interestingworld.entity.dummy;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class DummyEntity extends LivingEntity
{
	private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private ItemStack bodyArmor = ItemStack.EMPTY;

	public DummyEntity(EntityType<? extends LivingEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	public Iterable<ItemStack> getArmorItems()
	{
		return armorItems;
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot)
	{
		return switch (slot.getType())
		{
			case HAND -> this.handItems.get(slot.getEntitySlotId());
			case HUMANOID_ARMOR -> this.armorItems.get(slot.getEntitySlotId());
			case ANIMAL_ARMOR -> this.bodyArmor;
		};
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack)
	{
		this.processEquippedStack(stack);
		switch (slot.getType())
		{
			case HAND:
				this.onEquipStack(slot, this.handItems.set(slot.getEntitySlotId(), stack), stack);
				break;
			case HUMANOID_ARMOR:
				this.onEquipStack(slot, this.armorItems.set(slot.getEntitySlotId(), stack), stack);
				break;
			case ANIMAL_ARMOR:
				ItemStack itemStack = this.bodyArmor;
				this.bodyArmor = stack;
				this.onEquipStack(slot, itemStack, stack);
		}
	}

	@Override
	public Arm getMainArm()
	{
		return Arm.RIGHT;
	}
}
