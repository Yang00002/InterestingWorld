package org.yang.iw.util.constants;

import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.yang.iw.util.Base;

public class AttributeModifierIds
{
	public static Identifier of(Identifier identifier, StringIdentifiable slot)
	{
		return Identifier.of(identifier.getNamespace(), identifier.getPath() + ".modifier." + slot.asString());
	}

	public static Identifier of(Identifier identifier, StringIdentifiable slot, int idx)
	{
		return Identifier.of(identifier.getNamespace(),
				identifier.getPath() + ".modifier." + slot.asString() + "." + idx);
	}

	public static Identifier SWEEPING_DAMAGE_RATIO_ADD_MAIN_HAND_UPGRADE = Identifier.of(Base.MOD_ID, "swrt02u");
}
