package org.yang.iw.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ShortAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.yang.iw.IWRegistries;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.api.util.MutableAttributeValueDetail;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.boost.function.BoostFunctionMap;
import org.yang.iw.boost.function.LeveledSignalFunction;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.style.Color;
import org.yang.iw.util.style.TextStyle;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.yang.iw.util.IWCostUtil.getWorldLevelOfXpCost;
import static org.yang.iw.util.style.Color.GRAY_RGB;
import static org.yang.iw.util.style.Color.getLevelColor;
import static org.yang.iw.util.style.TextStyle.getNumberString;

public record BoostComponent(Object2ShortOpenHashMap<AbstractBoost> value, BoostFunctionMap functions, byte flag,
							 int cost) implements TooltipAppender
{
	// equal 函数比较值而非地址, 可以解决打开容器时手上和物品栏显示同样物品的问题
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BoostComponent that = (BoostComponent) o;
		return flag == that.flag && cost == that.cost && value.equals(that.value);
	}

	public static final BoostComponent DEFAULT = new BoostComponent(new Object2ShortOpenHashMap<>(),
			BoostFunctionMap.DEFAULT, (byte) 0, 0);
	private static final Codec<Object2ShortOpenHashMap<AbstractBoost>> VALUE_CODEC = Codec.unboundedMap(
			IWRegistries.BOOST.getCodec(), Codec.SHORT).xmap(Object2ShortOpenHashMap::new, Function.identity());
	private static final Codec<BoostComponent> FULL_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(VALUE_CODEC.fieldOf("value").forGetter(component -> component.value),
							Codec.BOOL.optionalFieldOf("is_store", false).forGetter(component -> ((component.flag & 1) == 0)),
							Codec.INT.optionalFieldOf("cost", 0).forGetter(component -> component.cost))
					.apply(instance, BoostComponent::createLegalWithCost));

	public static final Codec<BoostComponent> CODEC = Codec.withAlternative(FULL_CODEC, VALUE_CODEC,
			map -> createLegal(map, false));
	public static final PacketCodec<RegistryByteBuf, BoostComponent> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.map(Object2ShortOpenHashMap::new, PacketCodecs.registryValue(IWRegistryKeys.BOOST),
					PacketCodecs.SHORT), component -> component.value, PacketCodecs.BYTE, component -> component.flag,
			PacketCodecs.INTEGER, component -> component.cost, BoostComponent::createAsIfLegal);

	private static BoostComponent createAsIfLegal(Object2ShortOpenHashMap<AbstractBoost> value, byte flag, int cost)
	{
		if ((flag & 1) == 0) return new BoostComponent(value, BoostFunctionMap.DEFAULT, flag, cost);
		Object2ShortAVLTreeMap<AbstractBoost> treeMap = new Object2ShortAVLTreeMap<>(value);
		BoostFunctionMap.Builder map = new BoostFunctionMap.Builder();
		treeMap.forEach((i, j) -> map.add(i.getFunctions(j & 0xff)));
		return new BoostComponent(value, map.build(), flag, cost);
	}

	public void onTargetDamaged(ItemStack stack, ServerWorld world, LivingEntity target, DamageSource damageSource)
	{
		if (!is_store()) functions.applyForSlot(EquipmentSlot.MAINHAND,
				l -> l.applyTargetDamagedFunctions(f -> f.onTargetDamaged(stack, world, target, damageSource)));
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(EquipmentSlot.MAINHAND,
				l -> l.applyTargetDamagedFunctions(f -> f.onTargetDamaged(stack, world, target, damageSource)));
	}

	public int signalValue(LeveledSignalFunction.Signal signal, ItemStack stack, EquipmentSlot slot)
	{
		MutableInt mutableInt = new MutableInt();
		if (!is_store())
		{
			functions.applyForSlot(slot, l -> l.applyLeveledSignalFunctions(f -> {
				if (f.type() == signal) mutableInt.add(f.level());
			}));
		}
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(slot, l -> l.applyLeveledSignalFunctions(f -> {
			if (f.type() == signal) mutableInt.add(f.level());
		}));
		return mutableInt.getValue();
	}

	public int pretendedLevel(RegistryEntry<Enchantment> registryEntry, ItemStack stack, EquipmentSlot slot)
	{
		MutableFloat mutableFloat = new MutableFloat(0);
		if (!is_store())
		{
			functions.applyForSlot(EquipmentSlot.MAINHAND, l -> l.applyPretendEnchantInLootTableFunctions(f -> {
				if (registryEntry.matchesKey(f.type())) mutableFloat.add(f.level());
			}));
		}
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(EquipmentSlot.MAINHAND, l -> l.applyPretendEnchantInLootTableFunctions(f -> {
			if (registryEntry.matchesKey(f.type())) mutableFloat.add(f.level());
		}));
		return (int) mutableFloat.getValue().floatValue();
	}

	public float modifyKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource,
								 float baseKnockback)
	{
		MutableAttributeValueDetail detail = new MutableAttributeValueDetail(baseKnockback);
		if (!is_store())
		{
			functions.applyForSlot(EquipmentSlot.MAINHAND, l -> l.applyModifyKnockbackFunctions(
					f -> f.modifyKnockback(world, stack, target, damageSource, detail)));
		}
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(EquipmentSlot.MAINHAND, l -> l.applyModifyKnockbackFunctions(
				f -> f.modifyKnockback(world, stack, target, damageSource, detail)));
		return detail.value();
	}

	public void modifyDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource,
							 MutableAttributeValueDetail damage)
	{
		if (!is_store())
		{
			functions.applyForSlot(EquipmentSlot.MAINHAND,
					l -> l.applyModifyDamageFunctions(f -> f.modifyDamage(world, stack, target, damageSource,
							damage)));
		}
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(EquipmentSlot.MAINHAND,
				l -> l.applyModifyDamageFunctions(f -> f.modifyDamage(world, stack, target, damageSource, damage)));
	}

	public static int pretendedLevel(RegistryEntry<Enchantment> registryEntry, LivingEntity entity)
	{
		MutableFloat mutableFloat = new MutableFloat(0);
		for (var slot : EquipmentSlot.values())
		{
			var stack = entity.getEquippedStack(slot);
			if (!stack.isEmpty())
			{
				var component = stack.interestingWorld$getBoosts();
				if (!component.is_store())
				{
					component.functions.applyForSlot(slot, l -> l.applyPretendEnchantInLootTableFunctions(f -> {
						if (registryEntry.matchesKey(f.type())) mutableFloat.add(f.level());
					}));
				}
				var ub = stack.interestingWorld$uniqueBoost();
				if (ub != null) ub.applyForSlot(slot, l -> l.applyPretendEnchantInLootTableFunctions(f -> {
					if (registryEntry.matchesKey(f.type())) mutableFloat.add(f.level());
				}));
			}
		}
		return (int) mutableFloat.getValue().floatValue();
	}

	public int level()
	{
		return flag >> 2;
	}

	public int boostCount()
	{
		return value.size();
	}

	public boolean is_store()
	{
		return (flag & 1) == 0;
	}

	public boolean onlyDefault()
	{
		return (flag & 2) == 0;
	}

	public static BoostComponent createLegal(Object2ShortOpenHashMap<AbstractBoost> value, boolean is_store)
	{
		int cost = 0;
		int od = 0;
		for (Object2ShortOpenHashMap.Entry<AbstractBoost> entry : value.object2ShortEntrySet())
		{
			var boost = entry.getKey();
			short i = entry.getShortValue();
			int level = i & 0XFF;
			int def = i >> 8;
			if (level == 0) throw new IllegalArgumentException(
					"Enchantment " + Text.translatable(boost.translationKey()).getLiteralString() +
					" has invalid level " + level);
			short maxLevel = boost.maxAllowLevel();
			if (level > maxLevel)
			{
				if (def > maxLevel)
				{
					entry.setValue(maxLevel);
					cost += boost.xpCostOfLevel(maxLevel);
					od = 2;
				}
				else
				{
					entry.setValue((short) (maxLevel | (i & 0XFF00)));
					cost += boost.xpCostOfLevel((short) (maxLevel - def));
					if (def < maxLevel) od = 2;
				}
			}
			else if (def > level)
			{
				entry.setValue((short) (level));
				cost += boost.xpCostOfLevel((short) level);
			}
			else
			{
				cost += boost.xpCostOfLevel((short) (level - def));
				if (def < level) od = 2;
			}
		}
		return createAsIfLegal(value, (byte) ((getWorldLevelOfXpCost(cost) << 2) | od | (is_store ? 0 : 1)), cost);
	}

	public static BoostComponent createDefault(Object2ShortOpenHashMap<AbstractBoost> value)
	{
		Object2ShortOpenHashMap<AbstractBoost> map = new Object2ShortOpenHashMap<>();
		for (Object2ShortOpenHashMap.Entry<AbstractBoost> entry : value.object2ShortEntrySet())
		{
			var boost = entry.getKey();
			short i = entry.getShortValue();
			int level = i & 0XFF;
			if (level > 0)
			{
				short maxLevel = boost.maxAllowLevel();
				if (level > maxLevel) map.put(boost, (short) (maxLevel | (maxLevel << 8)));
				else map.put(boost, (short) (level | (level << 8)));
			}
		}
		return createAsIfLegal(map, (byte) 3, 0);
	}

	public static BoostComponent createLegalWithCost(Object2ShortOpenHashMap<AbstractBoost> value, boolean is_store,
													 int cost)
	{
		int custom_cost = cost;
		int od = 0;
		cost = 0;
		for (Object2ShortOpenHashMap.Entry<AbstractBoost> entry : value.object2ShortEntrySet())
		{
			var boost = entry.getKey();
			short i = entry.getShortValue();
			int level = i & 0XFF;
			int def = i >> 8;
			if (level == 0) throw new IllegalArgumentException(
					"Enchantment " + Text.translatable(boost.translationKey()).getLiteralString() +
					" has invalid level " + level);
			short maxLevel = boost.maxAllowLevel();
			if (level > maxLevel)
			{
				if (def > maxLevel)
				{
					entry.setValue(maxLevel);
					cost += boost.xpCostOfLevel(maxLevel);
					od = 2;
				}
				else
				{
					entry.setValue((short) (maxLevel | (i & 0XFF00)));
					cost += boost.xpCostOfLevel((short) (maxLevel - def));
					if (def < maxLevel) od = 2;
				}
			}
			else if (def > level)
			{
				entry.setValue((short) (level));
				cost += boost.xpCostOfLevel((short) level);
			}
			else
			{
				cost += boost.xpCostOfLevel((short) (level - def));
				if (def < level) od = 2;
			}
		}
		return createAsIfLegal(value, (byte) ((getWorldLevelOfXpCost(cost) << 2) | od | (is_store ? 0 : 1)),
				Math.max(custom_cost, cost));
	}



	public static BoostComponent ofDefault(Map<AbstractBoost, Short> value)
	{
		Object2ShortOpenHashMap<AbstractBoost> map = new Object2ShortOpenHashMap<>();
		value.forEach((i, j) -> {
			j = (short) (j & 0XFF);
			if (j < 1 || j > i.maxAllowLevel()) return;
			map.put(i, (short) ((j << 8) | j));
		});
		return createAsIfLegal(map, (byte) 1, 0);
	}

	public Text randomEntryText(int seed)
	{
		if (isEmpty()) return null;
		seed /= value.size();
		for (var entry : value.object2ShortEntrySet())
		{
			if (seed > 0) seed--;
			else return Text.translatable(entry.getKey().translationKey())
					.append(" " + TextStyle.getNumberString(entry.getShortValue()));
		}
		return null;
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type)
	{
		if (value.isEmpty()) return;
		int color;
		if (onlyDefault())
		{
			tooltip.accept(Text.translatable(TranslationPool.TOOLTIP_DEFAULT_ENCHANT).withColor(Color.GRAY_RGB));
			color = GRAY_RGB;
		}
		else color = getLevelColor(level());
		new Object2ShortAVLTreeMap<>(value).forEach((i, j) -> tooltip.accept(
				Text.translatable(i.translationKey()).append(" " + getNumberString(j & 0xFF)).withColor(color)));
	}

	public boolean isEmpty()
	{
		return value.isEmpty();
	}

	public void applyAttributeModifiers(ItemStack stack, EquipmentSlot slot, BiConsumer<RegistryEntry<EntityAttribute>
			, EntityAttributeModifier> attributeModifierConsumer)
	{
		if (!is_store()) functions.applyForSlot(slot,
				l -> l.applyAttributeModifierFunctions(f -> f.applyAttributeModifier(attributeModifierConsumer,
						slot)));
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(slot,
				l -> l.applyAttributeModifierFunctions(f -> f.applyAttributeModifier(attributeModifierConsumer,
						slot)));
	}

	public int getItemDamage(ServerWorld world, ItemStack stack, int damage)
	{
		MutableInt mutableInt = new MutableInt(damage);
		if (!is_store()) functions.applyForSlot(AttributeModifierSlot.ANY,
				l -> l.applyItemDamageFunctions(f -> f.getItemDamage(world, mutableInt)));
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(AttributeModifierSlot.ANY,
				l -> l.applyItemDamageFunctions(f -> f.getItemDamage(world, mutableInt)));
		return mutableInt.getValue();
	}

	public float getAcceleratedEnergyTransferRate(ItemStack stack)
	{
		float original = stack.getOrDefault(IWComponents.ENERGY_REGEN_RATE, 0f);
		MutableAttributeValueDetail detail = new MutableAttributeValueDetail(original);
		if (!is_store()) functions.applyForSlot(AttributeModifierSlot.ANY,
				l -> l.applyAccelerateEnergyTransferFunctions(f -> f.accelerateTransfer(detail)));
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(AttributeModifierSlot.ANY,
				l -> l.applyAccelerateEnergyTransferFunctions(f -> f.accelerateTransfer(detail)));
		return Math.clamp(detail.value(), 0f, 1f);
	}

	public int getModifiedMaxEnergy(ItemStack stack)
	{
		float original = stack.getOrDefault(IWComponents.MAX_ENERGY, 0f);
		MutableAttributeValueDetail detail = new MutableAttributeValueDetail(original);
		if (!is_store()) functions.applyForSlot(AttributeModifierSlot.ANY,
				l -> l.applyModifyMaxEnergyFunctions(f -> f.modifyMaxEnergy(detail)));
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(AttributeModifierSlot.ANY,
				l -> l.applyModifyMaxEnergyFunctions(f -> f.modifyMaxEnergy(detail)));
		return Math.max((int) detail.value(), 1);
	}

	public void applyAttributeModifiers(ItemStack stack, AttributeModifierSlot slot,
										BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer)
	{
		if (!is_store()) functions.applyForSlot(slot, l -> l.applyAttributeModifierFunctions(
				f -> f.attributeModifierForDisplay(attributeModifierConsumer, slot)));
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(slot, l -> l.applyAttributeModifierFunctions(
				f -> f.attributeModifierForDisplay(attributeModifierConsumer, slot)));
	}

	public void removeLocationBasedEffects(ItemStack stack, LivingEntity user, EquipmentSlot slot)
	{
		if (!is_store()) functions.applyForSlot(slot,
				l -> l.applyAttributeModifierFunctions(f -> f.removeAttributeModifier(stack, user, slot)));
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(slot,
				l -> l.applyAttributeModifierFunctions(f -> f.removeAttributeModifier(stack, user, slot)));
	}

	public void applyLocationBasedEffects(ItemStack stack, ServerWorld world, LivingEntity user, EquipmentSlot slot)
	{
		if (!is_store()) functions.applyForSlot(slot,
				l -> l.applyAttributeModifierFunctions(f -> f.applyAttributeModifier(world, stack, user, slot)));
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(slot,
				l -> l.applyAttributeModifierFunctions(f -> f.applyAttributeModifier(world, stack, user, slot)));
	}

	public boolean conflictWith(AbstractBoost boost)
	{
		for (var entry : value.object2ShortEntrySet())
		{
			var key = entry.getKey();
			if (boost.fatalConflictWith(key)) return true;
			var value = entry.getShortValue();
			if ((value >> 8) < (value & 0XFF) && boost.conflictWith(key)) return true;
		}
		return false;
	}

	public static short mergeLevel(AbstractBoost boost, short pre, short next)
	{
		int maxLevel = 0xFF & boost.maxAllowLevel();
		int defaultLevel = pre >> 8;
		if (maxLevel <= defaultLevel) return (short) ((maxLevel << 8) | maxLevel);
		int pn = (pre & 0XFF) - defaultLevel;
		int nn = next & 0XFF;
		if (pn < nn) return (short) (Math.min(nn, maxLevel) | (pre & 0xFF00));
		if ((pn > nn) || (pn == boost.maxTableLevel())) return (short) (Math.min(pn, maxLevel) | (pre & 0xFF00));
		else return (short) (Math.min(pn + 1, maxLevel) | (pre & 0xFF00));
	}

	public int applyTo(ItemStack stack)
	{
		BoostableComponent component = stack.getOrDefault(IWComponents.BOOSTABLE, BoostableComponent.DEFAULT);
		if (!component.isEmpty())
		{
			if (component.remainBoostTime() < level()) return -1;
			stack.set(IWComponents.BOOSTABLE, component.use(level()));
		}
		BoostComponent pre = stack.interestingWorld$getBoosts();
		Object2ShortOpenHashMap<AbstractBoost> map = new Object2ShortOpenHashMap<>(pre.value);
		int cost = this.cost;
		for (var entry : value.object2ShortEntrySet())
		{
			var boost = entry.getKey();
			var applyLevel = entry.getShortValue();
			short applyL = (short) (applyLevel & 0XFF);
			cost -= boost.xpCostOfLevel(applyL);
			if (pre.conflictWith(boost)) continue;
			short preLevel = pre.value.getOrDefault(boost, (short) 0);
			short preL = (short) (preLevel & 0XFF);
			short nextLevel = mergeLevel(boost, preLevel, applyLevel);
			short nextL = (short) (nextLevel & 0XFF);
			if (preL > nextL) continue;
			cost += boost.xpCostBetweenLevels(preL, nextL);
			map.put(boost, nextL);
		}
		stack.set(IWComponents.BOOST, createLegalWithCost(map, !pre.isEmpty() && pre.is_store(), cost + pre.cost));
		return cost;
	}

	public BoostComponent clear()
	{
		Object2ShortOpenHashMap<AbstractBoost> map = new Object2ShortOpenHashMap<>();
		for (Map.Entry<AbstractBoost, Short> entry : value.object2ShortEntrySet())
		{
			var abstractBoost = entry.getKey();
			var lvl = entry.getValue();
			int ld = lvl >> 8;
			if (ld > 0) map.put(abstractBoost, (short) ((ld << 8) | ld));
		}
		if (map.isEmpty()) return DEFAULT;
		return createLegal(map, is_store());
	}

	public int clearCost()
	{
		int cost = 0;
		for (var entry : value.object2ShortEntrySet())
		{
			var abstractBoost = entry.getKey();
			var lvl = entry.getShortValue();
			int l = (lvl & 0XFF) - (lvl >> 8);
			if (l > 0) cost += l * Math.min(is_store() ? 50 : 100,
					(int) ((is_store() ? 0.4f : 0.8f) * (abstractBoost.xpCostOfLevel((short) l) / l)));
		}
		return cost;
	}

	public void getEquipmentDropChance(ItemStack stack, EquipmentSlot slot, ServerWorld world, LivingEntity attacker,
									   DamageSource damageSource, MutableFloat baseEquipmentDropChance)
	{
		if (!is_store()) functions.applyForSlot(slot, l -> l.applyEquipmentDropChanceFunctions(
				f -> f.getEquipmentDropChance(world, attacker, damageSource, baseEquipmentDropChance)));
		var ub = stack.interestingWorld$uniqueBoost();
		if (ub != null) ub.applyForSlot(slot, l -> l.applyEquipmentDropChanceFunctions(
				f -> f.getEquipmentDropChance(world, attacker, damageSource, baseEquipmentDropChance)));
	}
}