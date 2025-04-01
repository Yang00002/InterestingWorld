package org.yang.iw.entity.player;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.yang.iw.IWEntityAttributes;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.boost.function.LeveledSignalFunction;
import org.yang.iw.component.IWComponents;
import org.yang.iw.item.tool.EnergyToolItem;
import org.yang.iw.network.bitio.BitWriter;
import org.yang.iw.network.payload.S2CPlayerDataPayload;
import org.yang.iw.util.IWUtil;
import org.yang.iw.util.constants.Numbers;

import static org.yang.iw.util.Return.RETURNTRUE;


public class IWServerPlayerData
{
	public final ServerPlayerEntity player;
	private AbstractAbility WeaponAbility = AbstractAbility.getDefault();
	private float current_energy = 0;
	private int last_energy = -1;
	private float energyRegenPool = 0;
	private boolean ability_on = true;
	private final AbilityCooldownManager cooldownManager = new AbilityCooldownManager(this);
	private final AbilityTickManager tickManager = new AbilityTickManager(this);
	private final int[] registers = new int[16];
	AbilityBarType barType = AbilityBarType.EMPTY;
	int clientStep = 0;

	public boolean sweeping = false;
	private boolean shouldSync = true;
	public int forging_seed = 0;
	private boolean tickOver = false;

	public IWServerPlayerData(ServerPlayerEntity player)
	{
		this.player = player;
	}

	public void sync()
	{
		shouldSync = true;
	}

	public boolean isAbilityOn()
	{
		return ability_on;
	}

	public AbstractAbility getWeaponAbility()
	{
		return WeaponAbility;
	}

	public void readNbt(NbtCompound nbt)
	{
		var component = nbt.getCompound("iw_player_data");
		current_energy = component.getFloat("current_energy");
		energyRegenPool = component.getFloat("energy_regen_pool");
		forging_seed = component.getInt("forging_seed");
		ability_on = component.getBoolean("ability_on");
		sweeping = component.getBoolean("sweeping");
		barType = AbilityBarType.values()[component.getInt("bar_type")];
		cooldownManager.readNbt(component.getCompound("cooldown"));
		tickManager.readNbt(component.getCompound("tick"));
		WeaponAbility = AbstractAbility.readNbt(component, "weapon_ability");
		importRegisters(component.getByteArray("ability_registers"));
	}

	public void copyPlayerData(IWServerPlayerData oldPlayerData, boolean alive, boolean keepInventory)
	{
		WeaponAbility = oldPlayerData.WeaponAbility;
		current_energy = (alive || keepInventory) ? oldPlayerData.current_energy
												  : Math.min(20, oldPlayerData.current_energy + 20); // TODO
		energyRegenPool = oldPlayerData.energyRegenPool;
		ability_on = oldPlayerData.ability_on;
		cooldownManager.copy(oldPlayerData.cooldownManager);
		tickManager.copy(oldPlayerData.tickManager);
		System.arraycopy(oldPlayerData.registers, 0, registers, 0, 16);
		barType = oldPlayerData.barType;
		clientStep = oldPlayerData.clientStep;
		sweeping = false;
		shouldSync = true;
		forging_seed = oldPlayerData.forging_seed;
		tickOver = oldPlayerData.tickOver;
	}

	public void writeNbt(NbtCompound nbt)
	{
		var component = new NbtCompound();
		component.putFloat("current_energy", current_energy);
		component.putFloat("energy_regen_pool", energyRegenPool);
		component.putInt("forging_seed", forging_seed);
		component.putBoolean("ability_on", ability_on);
		component.putBoolean("sweeping", sweeping);
		component.putInt("bar_type", barType.ordinal());
		component.put("cooldown", cooldownManager.writeNbt());
		component.put("tick", tickManager.writeNbt());
		WeaponAbility.writeNbt(component, "weapon_ability");
		component.putByteArray("ability_registers", exportRegisters());
		nbt.put("iw_player_data", component);
	}

	private void importRegisters(byte[] pre)
	{
		int len = Math.min(pre.length / 4, 16);
		for (int i = 0; i < len; i++)
		{
			int offset = i * 4;
			registers[i] = (((pre[offset] & 0xFF) << 24) | ((pre[offset + 1] & 0xFF) << 16) |
							((pre[offset + 2] & 0xFF) << 8) | (pre[offset + 3] & 0xFF));
		}
	}

	private byte[] exportRegisters()
	{
		byte[] byteArray = new byte[64];
		for (int i = 0; i < 16; i++)
		{
			int value = registers[i];
			int offset = i * 4;
			byteArray[offset] = (byte) ((value >> 24) & 0xFF);
			byteArray[offset + 1] = (byte) ((value >> 16) & 0xFF);
			byteArray[offset + 2] = (byte) ((value >> 8) & 0xFF);
			byteArray[offset + 3] = (byte) (value & 0xFF);
		}
		return byteArray;
	}

	public int loadRegister(int i)
	{
		return i >= 16 ? 0 : registers[i];
	}

	public void setRegister(int i, int value)
	{
		if (i >= 16) return;
		registers[i] = value;
	}

	public void updateEnergy(ServerPlayerEntity player, ItemStack stack)
	{
		if (player.getHungerManager().getFoodLevel() <= 6) return;
		float attr = ((float) player.getAttributeValue(IWEntityAttributes.ENERGY_REGENERATION)) * 0.05f;
		if (attr >= Numbers.FLOAT_EPSILON)
		{
			if (player.getHealth() >= player.getMaxHealth()) current_energy += attr;
			else current_energy += attr * 0.5f;
			if (current_energy > 20f)
			{
				energyRegenPool += current_energy - 20f;
				if (energyRegenPool >= 1f)
				{
					if (!stack.isEmpty() && stack.contains(IWComponents.MAX_ENERGY))
					{
						float max = IWUtil.Components.maxEnergy(stack);
						float cur = IWUtil.Components.currentEnergy(stack);
						float rate = stack.interestingWorld$getBoosts().getAcceleratedEnergyTransferRate(stack);
						if (max > cur && rate >= Numbers.FLOAT_EPSILON)
							stack.set(IWComponents.CURRENT_ENERGY, Math.min(max, cur + rate * energyRegenPool));
					}
					energyRegenPool = 0f;
				}
				current_energy = 20f;
			}
		}
	}

	public void setCooldown(AbstractAbility identifier, int duration)
	{
		cooldownManager.setCooldown(identifier, duration);
	}

	public boolean isInCooldown(AbstractAbility identifier)
	{
		return cooldownManager.isInCooldown(identifier);
	}

	public int coolDownStep(AbstractAbility ability)
	{
		return cooldownManager.cooldownStep(ability);
	}

	public void setBarType(AbilityBarType type)
	{
		barType = type;
		shouldSync = true;
	}

	public boolean tickOver()
	{
		return tickOver;
	}

	public void setTickOver(boolean t)
	{
		tickOver = t;
		if (tickOver)
		{
			shouldSync = true;
			WeaponAbility.onServerTickOver(player, this);
		}
	}

	public AbilityTickManager getTickManager()
	{
		return tickManager;
	}

	public void tick(ServerPlayerEntity player)
	{
		ItemStack stack = player.getWeaponStack();
		updateEnergy(player, stack);
		sweeping = false;
		if (!stack.isEmpty())
		{
			var ability = stack.getItem() instanceof EnergyToolItem ? stack.interestingWorld$getAbility().ability()
																	: AbstractAbility.getDefault();
			if (!ability.canWork())
			{
				if (!WeaponAbility.isEmpty())
				{
					WeaponAbility.onLeave(player, this);
					WeaponAbility = AbstractAbility.getDefault();
					shouldSync = true;
				}
			}
			else if (ability != WeaponAbility)
			{
				WeaponAbility.onLeave(player, this);
				ability.onEnter(player, this);
				WeaponAbility = ability;
				shouldSync = true;
			}
		}
		else if (!WeaponAbility.isEmpty())
		{
			WeaponAbility.onLeave(player, this);
			WeaponAbility = AbstractAbility.getDefault();
			shouldSync = true;
		}
		if (!cooldownManager.tick(WeaponAbility)) tickManager.tick();
		WeaponAbility.serverPlayerWeaponTick(player, this, stack);
		int ce = (int) current_energy;
		if (last_energy != ce) shouldSync = true;
		if (shouldSync)
		{
			shouldSync = false;
			ServerPlayNetworking.send(player, new S2CPlayerDataPayload(WeaponAbility, this, null));
		}
	}

	public AbilityBarType abilityBarType()
	{
		return barType;
	}

	public int getTickManagerStep()
	{
		return tickManager.tickProgress();
	}

	public boolean syncEnergy()
	{
		int cur = (int) current_energy;
		boolean ret = (cur != last_energy);
		last_energy = cur;
		return ret;
	}

	public boolean extractAtomicEnergy(ItemStack stack, float amount)
	{
		if (amount <= 0) return true;
		var ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork())
		{
			var fh = new MutableFloat(amount);
			var res = ab.extractPower(fh);
			if (res == RETURNTRUE)
			{
				return true;
			}
		}
		float e = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0.0f);
		if (e > 0.0f)
		{
			float fraction = stack.interestingWorld$getBoosts()
									 .signalValue(LeveledSignalFunction.Signal.ENERGY_SAVING, stack,
											 EquipmentSlot.MAINHAND) * 0.2f + 1;
			float nextAll = e * fraction;
			if (nextAll < amount)
			{
				amount -= nextAll;
				if (current_energy >= amount)
				{
					stack.set(IWComponents.CURRENT_ENERGY, 0.0f);
					current_energy -= amount;
					return true;
				}
			}
			else
			{
				stack.set(IWComponents.CURRENT_ENERGY, e - amount / fraction);
				return true;
			}
		}
		else if (current_energy >= amount)
		{
			current_energy -= amount;
			return true;
		}
		return false;
	}

	public boolean tryExtractAutomicEnergy(ItemStack stack, float amount)
	{
		if (amount <= 0) return true;
		var ab = stack.interestingWorld$getAbility().ability();
		if (ab.canWork())
		{
			var fh = new MutableFloat(amount);
			var res = ab.extractPower(fh);
			if (res == RETURNTRUE)
			{
				return true;
			}
		}
		float e = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0.0f);
		if (e > 0.0f)
		{
			float nextAll = e * (stack.interestingWorld$getBoosts()
										 .signalValue(LeveledSignalFunction.Signal.ENERGY_SAVING, stack,
												 EquipmentSlot.MAINHAND) * 0.2f + 1);
			if (nextAll < amount) return current_energy >= amount - nextAll;
			else return true;
		}
		return current_energy >= amount;
	}

	public void returnEnergyToTool(ItemStack stack, float amount)
	{
		float max = stack.getOrDefault(IWComponents.MAX_ENERGY, 0f);
		float cur = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0f);
		stack.set(IWComponents.CURRENT_ENERGY, Math.min(cur + amount, max));
	}

	public void returnEnergyToPlayerAndTool(ItemStack stack, float amount)
	{
		if (current_energy + amount >= 20f)
		{
			amount += current_energy - 20f;
			current_energy = 20f;
			if (amount > 0f)
			{
				float max = stack.getOrDefault(IWComponents.MAX_ENERGY, 0f);
				float cur = stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0f);
				stack.set(IWComponents.CURRENT_ENERGY, Math.min(cur + amount, max));
			}
		}
		else current_energy += amount;
	}

	public void returnEnergyToPlayer(float amount)
	{
		if (current_energy + amount >= 20f) current_energy = 20f;
		else current_energy += amount;
	}

	public float getCurrentEnergy()
	{
		return current_energy;
	}

	public int getLastEnergy()
	{
		return last_energy;
	}

	public void writeS2CInitializeDataToBuffer(BitWriter buf)
	{
		buf.writeBoolean(ability_on);
	}

	public void changeAbilityOpenCondition(boolean next, ServerPlayerEntity player)
	{
		if (ability_on != next)
		{
			if (!WeaponAbility.isEmpty()) shouldSync = true;
			if (next) WeaponAbility.onServerAbilityOpen(player, this);
			else WeaponAbility.onServerAbilityClose(player, this);
		}
		ability_on = next;
	}
}
