package org.yang.iw;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.yang.iw.api.register.DataGenSupplier;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.datagen.language.TranslationPool;

@DataGenSupplier
@IndependentRegister
public class IWEntityAttributes
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.BEFORE_INITIALIZE, LoadTime.Type.ON_INITIALIZE);
	}

	public static final RegistryEntry<EntityAttribute> ATTACK_DURATION_NEGLECT = register("duration_neglect",
			(new ClampedEntityAttribute(TranslationPool.add("attribute.name.duration_neglect", "攻击无敌帧忽略"), 0.0,
					-10.0, 20.0)).setTracked(true));

	public static final RegistryEntry<EntityAttribute> HURT_DAMAGE_MULTIPLIER = register("hurt_multiplier",
			(new ClampedEntityAttribute(TranslationPool.add("attribute.name.hurt_multiplier", "受伤乘数"), 1.0, 0.1,
					100.0)));

	private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute)
	{
		return Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla(id), attribute);
	}

	public static void initialize()
	{

	}
}
