package org.yang.iw.loot;

public class IWLoots
{
/*
	private static LootPoolEntryType registerType(String id, MapCodec<? extends LootPoolEntry> codec)
	{
		return Registry.register(Registries.LOOT_POOL_ENTRY_TYPE, Identifier.ofVanilla(id),
				new LootPoolEntryType(codec));
	}

	//public static LootPoolEntryType ENCHANT_RUNE_TYPE = registerType("ec_rune", EnchantRuneEntry.CODEC);

	static
	{
		LootTableEvents.MODIFY.register((lootTableRegistry, builder, lootTableSource, wrapperLookup) ->
		{
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
	}*/
}
