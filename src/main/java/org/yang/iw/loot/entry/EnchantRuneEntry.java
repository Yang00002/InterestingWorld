package org.yang.iw.loot.entry;

public class EnchantRuneEntry //extends LeafEntry
{
	/*

	public static final MapCodec<EnchantRuneEntry> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codec.INT.fieldOf("ll").forGetter(entry -> entry.min_level))
					.and(Codec.INT.fieldOf("lr").forGetter(entry -> entry.max_level)).and(Codec.INT.fieldOf("cl")
							.forGetter(entry -> HeartDataFlag.fromItem(entry.container).getMaterialLevel()))
					.and(addLeafFields(instance)).apply(instance, EnchantRuneEntry::new));
	private final int min_level;
	private final int max_level;
	private final BaseHeart container;

	protected EnchantRuneEntry(int l, int r, int containerLevel, int weight, int quality,
							   List<LootCondition> conditions, List<LootFunction> functions)
	{
		super(weight, quality, conditions, functions);
		if (l > r)
		{
			min_level = 0;
			max_level = 0;
			container = IWItems.COPPER_HEART;
			return;
		}
		min_level = Math.max(l, 0);
		max_level = Math.min(r, 10);
		container = BaseHeart.baseHeartSupportLevel(containerLevel);
	}

	@Override
	protected void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context)
	{
		var rd = context.getRandom();
		//var pos = context.get(LootContextParameters.ORIGIN);
		//if (pos != null && context.getWorld().getBiome(BlockPos.ofFloored(pos.x, pos.y, pos.z)).getKey().orElse
		// (null) ==
		//				   BiomeKeys.THE_VOID)
		//{
		//	lootConsumer.accept(
		//			RandomEnchantGenerator.generate(rd.nextInt(), rd.nextBetween(min_level + 3, max_level + 3)));
		//}
		//else
		lootConsumer.accept(
				RandomEnchantGenerator.generate(rd.nextInt(), rd.nextBetween(min_level, max_level), container,
						IWItems.HEART.getDefaultStack()));
	}

	@Override
	public LootPoolEntryType getType()
	{
		return ENCHANT_RUNE_TYPE;
	}

	public static LeafEntry.Builder<?> builder(int l, int r, int containerLevel)
	{
		return builder(
				(weight, quality, conditions, functions) -> new EnchantRuneEntry(l, r, containerLevel, weight, quality,
						conditions, functions));
	}*/
}
