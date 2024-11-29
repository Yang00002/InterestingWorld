package org.yang.interestingworld;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yang.interestingworld.item.tool.EnergyToolItem;
import org.yang.interestingworld.network.IWNetwork;
import org.yang.interestingworld.persistentdata.IWPersistentData;
import org.yang.interestingworld.playerdatamanager.ServerPlayerDataAccessor;
import org.yang.interestingworld.playerdatamanager.ServerPlayerDataManager;
import org.yang.interestingworld.rune.IWAbstractRuneAbility;
import org.yang.interestingworld.rune.IWRuneAbilitys;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_MODIFIER_ID;
import static org.yang.interestingworld.IWBlocks.addBlockItemToItemGroupWhenEnterWorld;
import static org.yang.interestingworld.IWItems.addItemToItemGroupWhenEnterWorld;
import static org.yang.interestingworld.IWResources.EnchantmentData.EnchantmentData;
import static org.yang.interestingworld.IWResources.ToolEnchantmentType.ToolEnchantmentType;
import static org.yang.interestingworld.IWUtil.Base.iwlogger;

public class IWUtil
{
	public static class EntityAbout
	{
		public static float getArmoredDamage(LivingEntity entity, DamageSource source, float amount)
		{
			if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR))
			{
				var st = entity.getStatusEffect(IWEffects.HURTING);
				if (st != null)
				{
					float mul = 1.0f;
					var am = st.getAmplifier() + 1;
					if (am != 1) mul = (1 + am / 10.0f);
					amount = DamageUtil.getDamageLeft(entity, amount * mul, source, (float) entity.getArmor(),
							(float) entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)) / mul;
				}
				else amount = DamageUtil.getDamageLeft(entity, amount, source, (float) entity.getArmor(),
						(float) entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
			}
			return amount;
		}

		public static void addHiddenStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect,
												 int durationTick)
		{
			StatusEffectInstance pre = entity.getStatusEffect(effect);
			if (pre == null)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0, false, false));
				return;
			}
			int duration = pre.getDuration();
			if (duration == -1) return;
			else if (durationTick == -1)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0, false, false));
				return;
			}
			int level = pre.getAmplifier() + 1;
			int nextDuration = (level * duration + durationTick) / level;
			entity.removeStatusEffectInternal(effect);
			entity.addStatusEffect(new StatusEffectInstance(effect, nextDuration, level - 1, false, false));
		}

		public static void addStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect, int durationTick)
		{
			StatusEffectInstance pre = entity.getStatusEffect(effect);
			if (pre == null)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0));
				return;
			}
			int duration = pre.getDuration();
			if (duration == -1) return;
			else if (durationTick == -1)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0));
				return;
			}
			int level = pre.getAmplifier() + 1;
			int nextDuration = (level * duration + durationTick) / level;
			entity.removeStatusEffectInternal(effect);
			entity.addStatusEffect(new StatusEffectInstance(effect, nextDuration, level - 1));
		}

		private static int keepConsistence(int level0, int time0, int level1, int time1, int consistenceTimeTick)
		{
			int d = time0 / consistenceTimeTick;
			int add = (level0 * time0) / level1;
			int t2 = time1 + add;
			int l = t2 - d;
			if (l < 0) return time1;
			int tl = l % consistenceTimeTick;
			if (tl >= add) return time1;
			else return t2 - tl;
		}

		public static void addHiddenStatusEffectWithConsistence(LivingEntity entity,
																RegistryEntry<StatusEffect> effect, int durationTick,
																int consistenceTimeTick)
		{
			StatusEffectInstance pre = entity.getStatusEffect(effect);
			if (pre == null)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0, false, false));
				return;
			}
			int preDuration = pre.getDuration();
			if (preDuration == -1) return;
			else if (durationTick == -1)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, 0, false, false));
				return;
			}
			int prelevel = pre.getAmplifier() + 1;
			entity.removeStatusEffectInternal(effect);
			if (prelevel > 1)

				entity.addStatusEffect(new StatusEffectInstance(effect,
						keepConsistence(1, durationTick, prelevel, preDuration, consistenceTimeTick), prelevel - 1,
						false, false));
			else entity.addStatusEffect(new StatusEffectInstance(effect,
					keepConsistence(prelevel, preDuration, 1, durationTick, consistenceTimeTick), 0, false, false));


		}

		public static void addHiddenStatusEffectWithConsistence(LivingEntity entity,
																RegistryEntry<StatusEffect> effect, int durationTick,
																int amplifier, int consistenceTimeTick)
		{
			StatusEffectInstance pre = entity.getStatusEffect(effect);
			if (pre == null)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
				return;
			}
			int preDuration = pre.getDuration();
			if (preDuration == -1 || durationTick == -1)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
				return;
			}
			int prelevel = pre.getAmplifier() + 1;
			entity.removeStatusEffectInternal(effect);
			if (prelevel > amplifier + 1)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect,
						keepConsistence(amplifier + 1, durationTick, prelevel, preDuration, consistenceTimeTick),
						prelevel - 1, false, false));
			}
			else entity.addStatusEffect(new StatusEffectInstance(effect,
					keepConsistence(prelevel, preDuration, amplifier + 1, durationTick, consistenceTimeTick),
					amplifier,
					false, false));

		}


		public static void addHiddenStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect,
												 int durationTick, int amplifier)
		{
			StatusEffectInstance pre = entity.getStatusEffect(effect);
			if (pre == null)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
				return;
			}
			int duration = pre.getDuration();
			if (duration == -1 || durationTick == -1)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
				return;
			}
			amplifier++;
			int level = pre.getAmplifier() + 1;
			int maxlevel = Math.max(level, amplifier);

			int nextDuration = (level * duration + durationTick * amplifier) / maxlevel;
			entity.removeStatusEffectInternal(effect);
			entity.addStatusEffect(new StatusEffectInstance(effect, nextDuration, maxlevel - 1, false, false));
		}

		public static void addStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect, int durationTick,
										   int amplifier)
		{
			StatusEffectInstance pre = entity.getStatusEffect(effect);
			if (pre == null)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier));
				return;
			}
			int duration = pre.getDuration();
			if (duration == -1 || durationTick == -1)
			{
				entity.addStatusEffect(new StatusEffectInstance(effect, durationTick, amplifier, false, false));
				return;
			}
			amplifier++;
			int level = pre.getAmplifier() + 1;
			int maxlevel = Math.max(level, amplifier);

			int nextDuration = (level * duration + durationTick * amplifier) / maxlevel;
			entity.removeStatusEffectInternal(effect);
			entity.addStatusEffect(new StatusEffectInstance(effect, nextDuration, maxlevel - 1));
		}
	}

	public static class MathFunc
	{
		public static Vec3d getVec3toEntity(LivingEntity entity, double x, double y, double z)
		{
			if (entity instanceof EnderDragonEntity) return new Vec3d(0, 0, 0);
			Box boundingBox = entity.getBoundingBox();
			double xhalf = boundingBox.getLengthX() / 2;
			double yhalf = boundingBox.getLengthY() / 2;
			double zhalf = boundingBox.getLengthZ() / 2;
			double pivoty = entity.getY() + yhalf;
			double pivotx = entity.getX();
			double pivotz = entity.getZ();
			double diffx = x - pivotx;
			double diffy = y - pivoty;
			double diffz = z - pivotz;
			int sigx = diffx >= 0 ? 1 : -1;
			int sigy = diffy >= 0 ? 1 : -1;
			int sigz = diffz >= 0 ? 1 : -1;
			double sigxhalf = sigx * xhalf;
			double sigyhalf = sigy * yhalf;
			double sigzhalf = sigz * zhalf;
			double tx = diffx / sigxhalf;
			double ty = diffy / sigyhalf;
			double tz = diffz / sigzhalf;
			double retx = tx > 1 ? sigxhalf - diffx : 0;
			double rety = ty > 1 ? sigyhalf - diffy : 0;
			double retz = tz > 1 ? sigzhalf - diffz : 0;
			return new Vec3d(retx, rety, retz);
		}

		@Deprecated
		public static Box getHorizontalAttackRangeBlock(double maxAngleCosin, double maxLength, double yExpanse,
														double x, double y, double z, double baseY,
														double NormalizedViewX, double NormalizedViewY,
														double NormalizedViewZ)
		{
			double viewX = NormalizedViewX * maxLength;
			double viewY = NormalizedViewY * maxLength;
			double viewZ = NormalizedViewZ * maxLength;
			double horizontalLengthsq = viewX * viewX + viewZ * viewZ;
			double Lengthsq = maxLength * maxLength;
			if (Lengthsq < 0.005)
			{
				if (viewY > 0) return new Box(x - yExpanse, y, z - yExpanse, x + yExpanse, y + maxLength,
						z + yExpanse);
				else return new Box(x - yExpanse, y - maxLength, z - yExpanse, x + yExpanse, y, z + yExpanse);
			}
			double cosinbeta = 1 - (1 - maxAngleCosin) * (Lengthsq / horizontalLengthsq);
			double sinbeta = Math.sqrt(1 - cosinbeta * cosinbeta);
			double xrange = Math.abs(sinbeta * viewZ);
			double zrange = Math.abs(sinbeta * viewX);
			double minX = x;
			double maxX = x;
			double minZ = z;
			double maxZ = z;
			if (viewX > 0)
			{
				maxX += viewX + xrange;
				minX -= xrange;
			}
			else
			{
				maxX += xrange;
				minX += viewX - xrange;
			}
			if (viewZ > 0)
			{
				maxZ += viewZ + zrange;
				minZ -= zrange;
			}
			else
			{
				maxZ += zrange;
				minZ += viewZ - zrange;
			}
			double yMultipiler = Math.sqrt(horizontalLengthsq / Lengthsq);
			double minY = baseY - yMultipiler;
			double maxY = y + yMultipiler;
			return new Box(minX, minY, minZ, maxX, maxY, maxZ);
		}

		public static Box getSphereAttackRangeBox(double maxLength, double x, double y, double z)
		{
			return new Box(x - maxLength - 2, y - maxLength - 6, z - maxLength - 2, x + maxLength + 2,
					y + maxLength + 2, z + maxLength + 2);
		}

		public static double squares(double x, double y, double z)
		{
			return x * x + y * y + z * z;
		}

		public static double squares(double x, double y)
		{
			return x * x + y * y;
		}

		public static double squares(double x)
		{
			return x * x;
		}


	}

	public static class EnergyTool
	{

		public static ServerPlayerDataManager getPlayerData(ServerPlayerEntity player)
		{
			return ((ServerPlayerDataAccessor) player).getDataManager();
		}

		// base + div, base - div, div, -div

		public interface DistancedEntityAttacker
		{
			void attack(LivingEntity attacker, LivingEntity entity, double distance);
		}

		public static void influenceEntityAround(LivingEntity attacker, double maxLength,
												 DistancedEntityAttacker dealer)
		{
			Vec3d eyeV = attacker.getEyePos();
			double x = eyeV.x;
			double y = eyeV.y;
			double z = eyeV.z;
			Box collectBox = MathFunc.getSphereAttackRangeBox(maxLength, x, y, z);
			World world = attacker.getWorld();
			List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, collectBox);
			for (LivingEntity entity : list)
			{
				if (entity == null) continue;
				if (entity == attacker) continue;
				if (entity instanceof TameableEntity tameableEntity)
				{
					if (tameableEntity.isOwner(attacker)) continue;
				}
				if (Saddleable.class.isAssignableFrom(entity.getClass()))
				{
					if (((Saddleable) entity).isSaddled()) continue;
				}
				if (attacker.isTeammate(entity)) continue;
				Vec3d dis = MathFunc.getVec3toEntity(entity, x, y, z);
				double len = dis.length();
				if (len <= maxLength) dealer.attack(attacker, entity, len);
			}
		}


		public static void sweepEntity(LivingEntity attacker, double maxAnglecosin, double maxLength,
									   LivingEntity target, DistancedEntityAttacker dealer)
		{
			Vec3d eyeV = attacker.getEyePos();
			double x = eyeV.x;
			double y = eyeV.y;
			double z = eyeV.z;
			Vec3d viewV = attacker.getRotationVector();
			viewV = viewV.normalize();
			double viewX = viewV.x;
			double viewY = viewV.y;
			double viewZ = viewV.z;
			Box collectBox = MathFunc.getSphereAttackRangeBox(maxLength, x, y, z);
			World world = attacker.getWorld();
			List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, collectBox);
			dealer.attack(attacker, target, eyeV.distanceTo(MathFunc.getVec3toEntity(target, x, y, z)));
			for (LivingEntity entity : list)
			{
				if (entity == null) continue;
				if (entity == attacker) continue;
				if (entity == target) continue;
				if (entity instanceof TameableEntity tameableEntity)
				{
					if (tameableEntity.isOwner(attacker)) continue;
				}
				if (Saddleable.class.isAssignableFrom(entity.getClass()))
				{
					if (((Saddleable) entity).isSaddled()) continue;
				}
				if (attacker.isTeammate(entity)) continue;
				Vec3d dis = MathFunc.getVec3toEntity(entity, x, y, z);
				double len = dis.length();
				double angle = (viewX * dis.x + viewY * dis.y + viewZ * dis.z) / len;
				if (len <= maxLength && (len <= 0.1 || angle >= maxAnglecosin)) dealer.attack(attacker, entity, len);
			}
		}
	}

	public static class Components
	{
		public static double setbaseAttackDamageModifier(ItemStack stack, double damage2inModifier,
														 double defaultvalue)
		{
			double ret = defaultvalue;
			AttributeModifiersComponent am = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, null);
			AttributeModifiersComponent.Builder newbuilder = AttributeModifiersComponent.builder();
			if (am != null)
			{
				var list = am.modifiers();
				for (var i : list)
				{
					var mod = i.modifier();
					if (mod.idMatches(BASE_ATTACK_DAMAGE_MODIFIER_ID))
					{
						ret = mod.value();
						newbuilder.add(i.attribute(),
								new EntityAttributeModifier(mod.id(), damage2inModifier, mod.operation()), i.slot());
					}
					else
					{
						newbuilder.add(i.attribute(), mod, i.slot());
					}
				}
			}
			else
			{
				newbuilder.add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
						new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, damage2inModifier,
								EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
			}
			stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, newbuilder.build());
			return ret;
		}

		public static void popEnchantments(ItemStack stack)
		{
			ItemEnchantmentsComponent before = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS,
					ItemEnchantmentsComponent.DEFAULT);
			if (before.isEmpty())
			{
				stack.remove(DataComponentTypes.ENCHANTMENTS);
				stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
				return;
			}
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(before);
			ItemEnchantmentsComponent after = builder.build();
			stack.set(DataComponentTypes.STORED_ENCHANTMENTS, after);
			stack.remove(DataComponentTypes.ENCHANTMENTS);
		}

		public static void pushEnchantments(ItemStack stack)
		{
			ItemEnchantmentsComponent before = stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS,
					ItemEnchantmentsComponent.DEFAULT);
			if (before.isEmpty())
			{
				stack.remove(DataComponentTypes.ENCHANTMENTS);
				stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
				return;
			}
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(before);
			ItemEnchantmentsComponent after = builder.build();
			stack.set(DataComponentTypes.ENCHANTMENTS, after);
			stack.remove(DataComponentTypes.STORED_ENCHANTMENTS);
		}

		public static boolean hasEnchantment(ItemStack stack)
		{
			var ec = stack.get(DataComponentTypes.ENCHANTMENTS);
			if (ec == null) return false;
			return !ec.isEmpty();
		}

		public static float maxEnergy(ItemStack stack)
		{
			return stack.getOrDefault(IWComponents.MAX_ENERGY, 0.0f);
		}

		public static float currentEnergy(ItemStack stack)
		{
			return stack.getOrDefault(IWComponents.CURRENT_ENERGY, 0.0f);
		}
	}

	public static class RuneAbility
	{
		private static CustomModelDataComponent getModelComponent(int id)
		{
			return new CustomModelDataComponent(id);
		}

		public static void setAbility(ItemStack stack, IWAbstractRuneAbility ability)
		{
			IWAbstractRuneAbility origin = getAbility(stack);
			if (origin.index != ability.index)
			{
				Item item = stack.getItem();
				stack.set(IWComponents.ABILITY_INDEX, ability.index);
				stack.set(IWComponents.ABILITY_COLOR_RGB, ability.getColor());
				if (item instanceof EnergyToolItem)
				{
					stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.toolIndex));
					origin.onRemoveAbility(stack);
					ability.onSetAbility(stack);
				}
				else stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.runeIndex));
			}
		}

		public static void setAbility(ItemStack stack, short index)
		{
			IWAbstractRuneAbility origin = getAbility(stack);
			IWAbstractRuneAbility ability = getAbility(index);
			if (origin.index != index)
			{
				Item item = stack.getItem();
				stack.set(IWComponents.ABILITY_INDEX, index);
				stack.set(IWComponents.ABILITY_COLOR_RGB, ability.getColor());
				if (item instanceof EnergyToolItem)
				{
					stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.toolIndex));
					origin.onRemoveAbility(stack);
					ability.onSetAbility(stack);
				}
				else stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, getModelComponent(ability.runeIndex));
			}
		}

		public static IWAbstractRuneAbility getAbility(ItemStack stack)
		{
			return IWRuneAbilitys.getAbilityofIndex(stack.getOrDefault(IWComponents.ABILITY_INDEX, (short) 0));
		}

		public static int getColor(ItemStack stack)
		{
			return stack.getOrDefault(IWComponents.ABILITY_COLOR_RGB, TextStyle.WHITE_RGB);
		}

		public static int getColor(short index)
		{
			return getAbility(index).getColor();
		}


		public static IWAbstractRuneAbility getAbility(short index)
		{
			return IWRuneAbilitys.getAbilityofIndex(index);
		}

		public static void removeToolAbility(ItemStack stack)
		{
			IWAbstractRuneAbility ability = getAbility(stack);
			stack.remove(IWComponents.ABILITY_INDEX);
			stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
			stack.set(IWComponents.ABILITY_COLOR_RGB, IWRuneAbilitys.DEFAULT_ABILITY.getColor());
			ability.onRemoveAbility(stack);
		}

		public static ItemStack getEmptyAbilityRune()
		{
			return IWItems.EMPTY_RUNE.getDefaultStack();
		}

		private static void removeToolAbility(ItemStack stack, IWAbstractRuneAbility ability)
		{
			stack.remove(IWComponents.ABILITY_INDEX);
			stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
			stack.set(IWComponents.ABILITY_COLOR_RGB, IWRuneAbilitys.DEFAULT_ABILITY.getColor());
			ability.onRemoveAbility(stack);
		}
	}

	public static class RuneEnchantment
	{
		public static final int HAVE_DEFAULT_ENCHANTMENT_BYTE = 1;
		public static final int HAVE_REAL_ENCHANTMENT_BYTE = 2;

		public static boolean onlyHaveDefaultEnchantment(ItemStack stack)
		{
			return haveDefaultEnchantment(stack) && !haveRealEnchantment(stack);
		}

		public static boolean haveDefaultEnchantment(ItemStack stack)
		{
			int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
			return (data & HAVE_DEFAULT_ENCHANTMENT_BYTE) != 0;
		}

		public static boolean haveRealEnchantment(ItemStack stack)
		{
			int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
			return (data & HAVE_REAL_ENCHANTMENT_BYTE) != 0;
		}

		public static void setFlagOfRealEnchant(ItemStack stack)
		{
			int data = stack.getOrDefault(IWComponents.DATA_FLAGS, 0);
			stack.set(IWComponents.DATA_FLAGS, (data & ~HAVE_DEFAULT_ENCHANTMENT_BYTE) | HAVE_REAL_ENCHANTMENT_BYTE);
		}


		public static int getEnchantmentLevel(World world, ItemStack stack, RegistryKey<Enchantment> key)
		{
			return stack.getEnchantments().getLevel(Registry.getEnchantmentEntry(world, key));
		}

		public static boolean isConflict(Collection<RegistryEntry<Enchantment>> enchantmentCollection,
										 RegistryEntry<Enchantment> target)
		{
			var tv = target.value();
			for (var i : enchantmentCollection)
			{
				var v = i.value();
				if (v == tv) continue;
				if (v.exclusiveSet().contains(target)) return true;
			}
			return false;
		}

		public static int canEnchantTo(ItemEnchantmentsComponent enchantmentsComponent, ItemStack stack, World world)
		{
			//寻找 stack 对应的物品类
			for (var tag : IWTags.EnergyToolTypeTags.getAll())
			{
				if (stack.isIn(tag))
				{
					//该类是否有附魔
					if (!ToolEnchantmentType.containsKey(tag)) return -1;
					//该类附魔
					Set<RegistryKey<Enchantment>> enchantmentSet = ToolEnchantmentType.get(tag);
					int cost = 0;
					Set<RegistryEntry<Enchantment>> applyedEnchantmentSet = new LinkedHashSet<>(
							stack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)
									.getEnchantments());
					var wrapper = IWUtil.Registry.getEnchantmentWrapper(world);
					for (var enchantmentKey : enchantmentSet)
					{
						//附魔是否被加载
						if (!EnchantmentData.containsKey(enchantmentKey))
						{
							enchantmentSet.remove(enchantmentKey);
							continue;
						}
						var optionalEnchantmentEntry = wrapper.getOptional(enchantmentKey);
						//附魔是否存在
						if (optionalEnchantmentEntry.isEmpty())
						{
							enchantmentSet.remove(enchantmentKey);
						}
						else
						{
							var enchantmentEntry = optionalEnchantmentEntry.get();
							//附魔在符文上
							int applyLevel = enchantmentsComponent.getLevel(enchantmentEntry);
							if (applyLevel == 0) continue;
							var data = EnchantmentData.get(enchantmentKey);
							//默认附魔未达到最大等级
							int deflevel = stack.getEnchantments().getLevel(enchantmentEntry);
							if (deflevel > data.getMaxAllowLevel()) continue;
							int addLevel = Math.min(data.getMaxAllowLevel() - deflevel, applyLevel);
							if (!isConflict(applyedEnchantmentSet, enchantmentEntry))
								cost += data.getLevelCost(addLevel);
							else if (data.allowConflict())
								cost += data.getLevelCost(addLevel) + data.getConflictCostPunishment();
						}
					}
					if (cost != 0) return cost;
				}
			}
			return -1;
		}

		public static void applyEnchant(ItemEnchantmentsComponent enchantmentsComponent, ItemStack toolStack,
										World world)
		{
			//寻找 stack 对应的物品类
			for (var tag : IWTags.EnergyToolTypeTags.getAll())
			{
				if (toolStack.isIn(tag))
				{
					//该类是否有附魔
					if (!ToolEnchantmentType.containsKey(tag)) return;
					//该类附魔
					Set<RegistryKey<Enchantment>> enchantmentSet = ToolEnchantmentType.get(tag);
					ItemEnchantmentsComponent before = toolStack.getOrDefault(IWComponents.DEFAULT_ENCHANTMENTS,
							ItemEnchantmentsComponent.DEFAULT);
					ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(before);
					var wrapper = IWUtil.Registry.getEnchantmentWrapper(world);
					for (var enchantmentKey : enchantmentSet)
					{
						//附魔是否被加载
						if (!EnchantmentData.containsKey(enchantmentKey))
						{
							enchantmentSet.remove(enchantmentKey);
							continue;
						}
						var optionalEnchantmentEntry = wrapper.getOptional(enchantmentKey);
						//附魔是否存在
						if (optionalEnchantmentEntry.isEmpty())
						{
							enchantmentSet.remove(enchantmentKey);
						}
						else
						{
							var enchantmentEntry = optionalEnchantmentEntry.get();
							//附魔在符文上
							int applyLevel = enchantmentsComponent.getLevel(enchantmentEntry);
							if (applyLevel == 0) continue;
							var data = EnchantmentData.get(enchantmentKey);
							//默认附魔未达到最大等级
							int deflevel = toolStack.getEnchantments().getLevel(enchantmentEntry);
							if (deflevel > data.getMaxAllowLevel()) continue;
							int targetLevel = Math.min(data.getMaxAllowLevel(), applyLevel + deflevel);
							if (isConflict(builder.getEnchantments(), enchantmentEntry) && !data.allowConflict())
								continue;
							builder.set(enchantmentEntry, targetLevel);
						}
					}
					toolStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
					setFlagOfRealEnchant(toolStack);
				}
			}
		}
	}

	public static class TextStyle
	{
		public static String numberToString(float f)
		{
			String s = String.format("%.1f", f);
			if (s.endsWith(".0")) s = String.format("%.0f", f);
			return s;
		}

		public static String numberToString(double f)
		{
			String s = String.format("%.1f", f);
			if (s.endsWith(".0")) s = String.format("%.0f", f);
			return s;
		}

		public static final int GRAY_RGB = 0XAAAAAA;
		public static final int WHITE_RGB = 0XFFFFFF;
		public static final int PURE_GREEN_RGB = 0X00FF00;
		public static final int RED_RGB = 0xFF5555;
		public static final int PINK_RGB = 0Xff738b;
		public static final int YELLOW_RGB = 0XFFFF55;
		public static final int CYAN_RGB = 0X55FFFF;

		public static Style getBoldTextStyle(int colorRGB)
		{
			if (colorRGB > 0) return Style.EMPTY.withColor(colorRGB).withBold(true);
			return Style.EMPTY.withBold(true);
		}
	}

	public static class Base
	{
		public static final String MOD_ID = "interestingworld";
		public static final Logger iwlogger = LogManager.getLogger();

	}

	public static class Server
	{
		private static MinecraftServer currentServer = null;

		private static IWPersistentData persistentData = null;

		private static void onServerStarting(MinecraftServer server)
		{
			currentServer = server;
		}

		private static void onServerStarted(MinecraftServer server)
		{
			iwlogger.info("server Start");
			persistentData = IWPersistentData.getServerState(currentServer);
			addItemToItemGroupWhenEnterWorld();
			addBlockItemToItemGroupWhenEnterWorld();
		}

		private static void onServerStopped(MinecraftServer server)
		{
			iwlogger.info("server Stop");
			if (currentServer == server)
			{
				currentServer = null;
				persistentData = null;
			}
		}

		public static MinecraftServer getCurrentServer()
		{
			return currentServer;
		}

		public static IWPersistentData getPersistentData()
		{
			return persistentData;
		}

		public static void initialize()
		{
			ServerLifecycleEvents.SERVER_STARTING.register(Server::onServerStarting);
			ServerLifecycleEvents.SERVER_STOPPED.register(Server::onServerStopped);
			ServerLifecycleEvents.SERVER_STARTED.register(Server::onServerStarted);

		}
	}

	public static class Return
	{
		public static final byte PASS = 0;
		public static final byte SUCCESS = 1;
		public static final byte FAIL = 2;
		public static final byte IGNORE = 3;
		public static final byte CONSUME = 4;
		public static final byte RETURNTRUE = 5;

		public static class FloatHolder
		{
			public float f;

			public FloatHolder(float t)
			{
				f = t;
			}

			public static FloatHolder getInstance(float f)
			{
				return new FloatHolder(f);
			}
		}

		public static class AbilityItemBarMessageTaker
		{
			public int baseColor;
			public int contentColor;
			public int step;
		}
	}

	public static class Registry
	{
		//不安全
		public static RegistryEntry<Enchantment> getEnchantmentEntry(World world, RegistryKey<Enchantment> key)
		{
			return world.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(key);
		}

		public static RegistryWrapper<Enchantment> getEnchantmentWrapper(World world)
		{
			return world.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		}

		public static RegistryEntry<Enchantment> getEnchantmentEntry(RegistryWrapper.WrapperLookup wrapperLookup,
																	 RegistryKey<Enchantment> key)
		{
			return wrapperLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(key);
		}

		public static DamageSource createDamageSource(World world, RegistryKey<DamageType> damagetype,
													  LivingEntity attacker)
		{
			return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(damagetype),
					attacker);
		}

		public static DamageSource createDamageSource(World world, RegistryKey<DamageType> damagetype)
		{
			return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(damagetype));
		}

		public static StatusEffectInstance createStatusEffect(RegistryEntry<StatusEffect> effect, int durationTick,
															  int amplifier)
		{
			return new StatusEffectInstance(effect, durationTick, amplifier);
		}

		public static StatusEffectInstance createStatusEffect(RegistryEntry<StatusEffect> effect, int durationTick)
		{
			return new StatusEffectInstance(effect, durationTick);
		}
	}

	public static class Network
	{
		public static void spawnItemParticle(Entity entity, ItemStack stack)
		{
			if (entity instanceof ServerPlayerEntity)
			{
				ServerPlayNetworking.send((ServerPlayerEntity) entity,
						new IWNetwork.ItemBreakParticlePayload(new ItemStackParticleEffect(ParticleTypes.ITEM,
								stack)));
			}
			else iwlogger.warn("spawnItemParticle 尝试将 ClientPlayerEntity 转为 ServerPlayerEntity");
		}

		public static void spawnDamageIndicatorParticle(Entity entity, int amount)
		{
			World world = entity.getWorld();
			if (world instanceof ServerWorld)
			{
				((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(),
						entity.getBodyY(0.5), entity.getZ(), amount, 0.1, 0.0, 0.1, 0.2);
			}
			else iwlogger.warn("spawnDamageIndicatorParticle 尝试将 ClientWorld 转为 ServerWorld");
		}

		public static void spawnDamageIndicatorParticle(Entity entity, float damage)
		{
			World world = entity.getWorld();
			if (world instanceof ServerWorld)
			{
				((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(),
						entity.getBodyY(0.5), entity.getZ(), (int) damage, 0.1, 0.0, 0.1, 0.2);
			}
			else iwlogger.warn("spawnDamageIndicatorParticle 尝试将 ClientWorld 转为 ServerWorld");
		}

		public static void spawnParticleAtEntity(Entity entity, ParticleEffect particle)
		{
			World world = entity.getWorld();
			if (world instanceof ServerWorld)
			{
				((ServerWorld) world).spawnParticles(particle, entity.getX(), entity.getBodyY(0.5), entity.getZ(), 1
						, 0,
						0, 0, 0);
			}
			else iwlogger.warn("spawnParticleAtEntity 尝试将 ClientWorld 转为 ServerWorld");
		}

		public static void spawnParticleAtPos(World world, ParticleEffect particle, double x, double y, double z)
		{
			if (world instanceof ServerWorld) ((ServerWorld) world).spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
			else iwlogger.warn("spawnParticleAtPos 尝试将 ClientWorld 转为 ServerWorld");
		}

		public static void spawnParticlesAtPos(World world, ParticleEffect particle, double x, double y, double z,
											   int count, double dx, double dy, double dz)
		{
			if (world instanceof ServerWorld)
				((ServerWorld) world).spawnParticles(particle, x, y, z, count, dx, dy, dz, 0);
			else iwlogger.warn("spawnParticleAtPos 尝试将 ClientWorld 转为 ServerWorld");
		}

		public static void spawnParticleAtPos(World world, ParticleEffect particle, double x, double y, double z,
											  int count, double dx, double dy, double dz, double speed)
		{
			if (world instanceof ServerWorld)
				((ServerWorld) world).spawnParticles(particle, x, y, z, count, dx, dy, dz, speed);
			else iwlogger.warn("spawnParticleAtPos 尝试将 ClientWorld 转为 ServerWorld");
		}

		public static void playSoundAtEntity(Entity entity, SoundEvent sound, SoundCategory category)
		{
			World world = entity.getWorld();
			if (world instanceof ServerWorld)
			{
				world.playSound(null, entity.getX(), entity.getBodyY(0.5), entity.getZ(), sound, category, 1.0f, 1.0f,
						0);
			}
			else iwlogger.warn("playSoundAtEntity 尝试将 ClientWorld 转为 ServerWorld");
		}

		public static void playSoundAtPos(World world, double x, double y, double z, SoundEvent sound,
										  SoundCategory category)
		{
			if (world instanceof ServerWorld)
			{
				world.playSound(null, x, y, z, sound, category, 1.0f, 1.0f, 0);
			}
			else iwlogger.warn("playSoundAtPos 尝试将 ClientWorld 转为 ServerWorld");
		}

		public static void playSoundToPlayer(PlayerEntity player, SoundEvent sound, SoundCategory category)
		{
			if (player instanceof ServerPlayerEntity) player.playSoundToPlayer(sound, category, 1.0f, 1.0f);
			else iwlogger.warn("playSoundToPlayer 尝试将 ClientPlayerEntity 转为 ServerPlayerEntity");
		}

		public static void playSoundToPlayer(PlayerEntity player, SoundEvent sound, SoundCategory category, int delay)
		{
			if (player instanceof ServerPlayerEntity sp)
			{
				ServerPlayNetworking.send(sp,
						new IWNetwork.DeferSoundPayload(Registries.SOUND_EVENT.getEntry(sound), category,
								(float) sp.getX(), (float) sp.getY(), (float) sp.getZ(), 1.0f, 1.0f, delay));
			}
			else iwlogger.warn("playSoundToPlayer 尝试将 ClientPlayerEntity 转为 ServerPlayerEntity");
		}
	}
}
