package org.yang.interestingworld.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.function.LootFunction;
import org.yang.interestingworld.enchant.RandomEnchantGenerator;

import java.util.List;
import java.util.function.Consumer;

import static org.yang.interestingworld.IWLoots.ENCHANT_RUNE_TYPE;

public class EnchantRuneEntry extends LeafEntry
{

	public static final MapCodec<EnchantRuneEntry> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codec.INT.fieldOf("ll").forGetter(entry -> entry.min_level))
					.and(Codec.INT.fieldOf("lr").forGetter(entry -> entry.max_level)).and(addLeafFields(instance))
					.apply(instance, EnchantRuneEntry::new));
	private final int min_level;
	private final int max_level;

	protected EnchantRuneEntry(int l, int r, int weight, int quality, List<LootCondition> conditions,
							   List<LootFunction> functions)
	{
		super(weight, quality, conditions, functions);
		if (l > r)
		{
			min_level = 0;
			max_level = 0;
			return;
		}
		min_level = Math.max(l, 0);
		max_level = Math.min(r, 10);
	}

	@Override
	protected void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context)
	{
		var rd = context.getRandom();
		lootConsumer.accept(RandomEnchantGenerator.generate(rd.nextInt(), rd.nextBetween(min_level, max_level)));
	}

	@Override
	public LootPoolEntryType getType()
	{
		return ENCHANT_RUNE_TYPE;
	}

	public static LeafEntry.Builder<?> builder(int l, int r)
	{
		return builder(
				(weight, quality, conditions, functions) -> new EnchantRuneEntry(l, r, weight, quality, conditions,
						functions));
	}
}
