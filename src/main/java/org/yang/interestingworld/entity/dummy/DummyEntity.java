package org.yang.interestingworld.entity.dummy;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import static org.yang.interestingworld.IWUtil.TextStyle.numberToString;

public class DummyEntity extends LivingEntity
{
	private static final class DamageStatistic
	{
		private boolean preTraceMax = false;
		private int tick = 0;
		private int noDamageSecond = 0;
		private int forgetSecond = 9;
		private float allDamage = 0;
		private float damageThatSecond = 0;
		private float averageDamage = 0;
		private int secondInvolve = 0;
		private float preAverageDamage = 0.001f;
		private float preMaxDamage = -1;
		private float maxDamage = 0;

		public void damage(float amount)
		{
			damageThatSecond += amount;
			if (amount > maxDamage) maxDamage = amount;
		}

		public void heal(float amount)
		{
			damageThatSecond -= amount;
		}

		public void tick()
		{
			tick++;
			if (tick >= 20)
			{
				tick = 0;
				if (damageThatSecond == 0)
				{
					if (noDamageSecond >= forgetSecond)
					{
						allDamage = 0;
						averageDamage = 0;
						secondInvolve = 0;
						maxDamage = 0;
						return;
					}
					else noDamageSecond++;
					secondInvolve++;
				}
				else
				{
					noDamageSecond = 0;
					allDamage += damageThatSecond;
					damageThatSecond = 0;
					secondInvolve++;
					averageDamage = allDamage / secondInvolve;
				}
			}
		}

		public void setForgetSecond(int forgetSecond1)
		{
			forgetSecond = forgetSecond1;
		}

		public float getAverageDamage()
		{
			return averageDamage;
		}

		public float getMaxDamage()
		{
			return maxDamage;
		}

		public boolean shouldUpdate(boolean traceMax)
		{
			if (preTraceMax != traceMax)
			{
				preTraceMax = traceMax;
				preMaxDamage = maxDamage;
				preAverageDamage = averageDamage;
				return true;
			}
			if (tick == 0)
			{
				if (traceMax)
				{
					if (preMaxDamage != maxDamage)
					{
						preMaxDamage = maxDamage;
						return true;
					}
				}
				else if (preAverageDamage != averageDamage)
				{
					preAverageDamage = averageDamage;
					return true;
				}
			}
			return false;
		}

	}

	private boolean traceMax = false;
	private final DamageStatistic damageStatistic = new DamageStatistic();
	private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);

	public DummyEntity(EntityType<? extends LivingEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	public void heal(float amount)
	{
		damageStatistic.heal(amount);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("trace_max")) traceMax = nbt.getBoolean("trace_max");
		if (nbt.contains("ArmorItems", NbtElement.LIST_TYPE))
		{
			NbtList nbtList = nbt.getList("ArmorItems", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < this.armorItems.size(); i++)
			{
				NbtCompound nbtCompound = nbtList.getCompound(i);
				this.armorItems.set(i, ItemStack.fromNbtOrEmpty(this.getRegistryManager(), nbtCompound));
			}
		}
		if (nbt.contains("HandItems", NbtElement.LIST_TYPE))
		{
			NbtList nbtList = nbt.getList("HandItems", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < this.handItems.size(); i++)
			{
				NbtCompound nbtCompound = nbtList.getCompound(i);
				this.handItems.set(i, ItemStack.fromNbtOrEmpty(this.getRegistryManager(), nbtCompound));
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("trace_max", traceMax);
		NbtList nbtList = new NbtList();

		for (ItemStack itemStack : this.armorItems)
		{
			if (!itemStack.isEmpty())
			{
				nbtList.add(itemStack.encode(this.getRegistryManager()));
			}
			else
			{
				nbtList.add(new NbtCompound());
			}
		}

		nbt.put("ArmorItems", nbtList);
		NbtList nbtList3 = new NbtList();

		for (ItemStack itemStack2 : this.handItems)
		{
			if (!itemStack2.isEmpty())
			{
				nbtList3.add(itemStack2.encode(this.getRegistryManager()));
			}
			else
			{
				nbtList3.add(new NbtCompound());
			}
		}

		nbt.put("HandItems", nbtList3);
	}

	@Override
	public void baseTick()
	{
		super.baseTick();
		if (this.isAlive() && !this.getWorld().isClient)
		{
			damageStatistic.tick();
			if (damageStatistic.shouldUpdate(traceMax))
			{
				if (traceMax) setCustomName(Text.literal(numberToString(damageStatistic.getMaxDamage())));
				else setCustomName(Text.literal(numberToString(damageStatistic.getAverageDamage())));
			}
		}
	}

	@Override
	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand)
	{
		if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
		ItemStack stack = player.getStackInHand(hand);
		if (stack.isEmpty()) return ActionResult.PASS;
		Item it = stack.getItem();
		if (it == Items.SHIELD)
		{
			if (getEquippedStack(EquipmentSlot.OFFHAND).getItem() == Items.SHIELD)
			{
				var at = this.getAttributes();
				var kn = at.getCustomInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
				if (kn != null) kn.setBaseValue(0.0);
				equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
				return ActionResult.SUCCESS;
			}
			var at = this.getAttributes();
			var kn = at.getCustomInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
			if (kn != null) kn.setBaseValue(1.0);
			equipStack(EquipmentSlot.OFFHAND, Items.SHIELD.getDefaultStack());
			return ActionResult.SUCCESS;
		}
		else if (it == Items.BLAZE_POWDER)
		{
			this.setHealth(this.getMaxHealth());
			return ActionResult.SUCCESS;
		}
		else if (it == Items.MILK_BUCKET)
		{
			this.clearStatusEffects();
			return ActionResult.SUCCESS;
		}
		else if (it == Items.IRON_SWORD)
		{
			if (getEquippedStack(EquipmentSlot.MAINHAND).getItem() == Items.IRON_SWORD)
			{
				traceMax = false;
				equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				return ActionResult.SUCCESS;
			}
			traceMax = true;
			equipStack(EquipmentSlot.MAINHAND, Items.IRON_SWORD.getDefaultStack());
			return ActionResult.SUCCESS;
		}
		else
		{
			var slot = getPreferredEquipmentSlot(stack);
			if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.FEET || slot == EquipmentSlot.LEGS ||
				slot == EquipmentSlot.CHEST)
			{
				if (this.getEquippedStack(slot).getItem() == it)
				{
					this.equipStack(slot, ItemStack.EMPTY);
				}
				else
				{
					this.equipStack(slot, stack.copy());
				}
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public Iterable<ItemStack> getArmorItems()
	{
		return armorItems;
	}

	@Override
	protected void applyDamage(DamageSource source, float amount)
	{
		if (!this.isInvulnerableTo(source))
		{
			amount = this.applyArmorToDamage(source, amount);
			amount = this.modifyAppliedDamage(source, amount);
			float var9 = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
			float g = amount - var9;
			if (g > 0.0F && g < 3.4028235E37F && source.getAttacker() instanceof ServerPlayerEntity serverPlayerEntity)
			{
				serverPlayerEntity.increaseStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(g * 10.0F));
			}
			if (var9 != 0.0F)
			{
				this.getDamageTracker().onDamage(source, var9);
				damageStatistic.damage(var9);
				if (source.getAttacker() instanceof ServerPlayerEntity serverPlayerEntity)
				{
					Item it = serverPlayerEntity.getWeaponStack().getItem();
					if (it == Items.STICK)
					{
						this.setHealth(0);
					}
					else if (it == Items.BLAZE_ROD)
					{
						this.setHealth(this.getHealth() - 1.0f);
					}
				}
				if (source.isOf(DamageTypes.OUTSIDE_BORDER) || source.isOf(DamageTypes.OUT_OF_WORLD)) this.setHealth(0);
				this.emitGameEvent(GameEvent.ENTITY_DAMAGE);
			}
		}
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot)
	{
		return switch (slot.getType())
		{
			case HAND -> this.handItems.get(slot.getEntitySlotId());
			case HUMANOID_ARMOR -> this.armorItems.get(slot.getEntitySlotId());
			case ANIMAL_ARMOR -> ItemStack.EMPTY;
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
		}
	}

	@Override
	public Arm getMainArm()
	{
		return Arm.RIGHT;
	}

	@Override
	public boolean canUseSlot(EquipmentSlot slot)
	{
		switch (slot)
		{
			case EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST,
					EquipmentSlot.LEGS, EquipmentSlot.FEET ->
			{
				return true;
			}
		}
		return false;
	}
}
