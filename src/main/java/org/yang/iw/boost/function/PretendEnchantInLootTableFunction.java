package org.yang.iw.boost.function;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;

public abstract class PretendEnchantInLootTableFunction
{

	public abstract RegistryKey<Enchantment> type();

	public abstract float level();
}
