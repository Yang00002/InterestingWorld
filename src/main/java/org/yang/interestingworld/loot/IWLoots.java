package org.yang.interestingworld.loot;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.loot.entry.EnchantRuneEntry;

public class IWLoots
{
	private static LootPoolEntryType registerType(String id, MapCodec<? extends LootPoolEntry> codec)
	{
		return Registry.register(Registries.LOOT_POOL_ENTRY_TYPE, Identifier.ofVanilla(id),
				new LootPoolEntryType(codec));
	}

	public static LootPoolEntryType ENCHANT_RUNE_TYPE = registerType("ec_rune", EnchantRuneEntry.CODEC);

	public static void initialize()
	{
		LootTableEvents.MODIFY.register((lootTableRegistry, builder, lootTableSource, wrapperLookup) -> {
			if (lootTableRegistry == LootTables.DESERT_PYRAMID_CHEST)
			{
				builder.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
						.with(EmptyEntry.builder().weight(80)).with(EnchantRuneEntry.builder(0, 1, 1).weight(20)));
			}
			else if (lootTableRegistry == LootTables.ABANDONED_MINESHAFT_CHEST)
			{
				builder.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
						.with(EmptyEntry.builder().weight(90)).with(EnchantRuneEntry.builder(0, 2, 2).weight(10)));
			}
			else if (lootTableRegistry == LootTables.ANCIENT_CITY_CHEST)
			{
				builder.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1))
						.with(EmptyEntry.builder().weight(90)).with(EnchantRuneEntry.builder(1, 4, 4).weight(10)));
			}
		});
	}
}
