package org.yang.iw.ability;

import net.minecraft.util.Identifier;
import org.yang.iw.util.Base;

public enum AbilityCooldownGroup
{
	EMPTY(of("empty")), ATTACK(of("attack")), ASSISTANCE(of("assistance"));

	public final Identifier id;

	AbilityCooldownGroup(Identifier id)
	{
		this.id = id;
	}

	private static Identifier of(String string)
	{
		return Identifier.of(Base.MOD_ID, string);
	}

	public static AbilityCooldownGroup groupOf(Identifier identifier)
	{
		var values = values();
		for (AbilityCooldownGroup value : values) if (value.id.compareTo(identifier) == 0) return value;
		return EMPTY;
	}
}
