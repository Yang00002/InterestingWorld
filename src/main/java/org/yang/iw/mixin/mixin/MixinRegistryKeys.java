package org.yang.iw.mixin.mixin;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.mixin.mixin_interface.InterfaceRegistryKeys;
import org.yang.iw.tool.material.ToolMaterial;
import org.yang.iw.upgrade.AbstractUpgrade;

@Mixin(RegistryKeys.class)
public abstract class MixinRegistryKeys implements InterfaceRegistryKeys
{
	@Shadow
	private static <T> RegistryKey<Registry<T>> of(String id)
	{
		return null;
	}

	@Unique
	private static final RegistryKey<Registry<AbstractAbility>> ABILITY = of("ability");
	@Unique
	private static final RegistryKey<Registry<AbstractBoost>> BOOST = of("boost");
	@Unique
	private static final RegistryKey<Registry<AbstractUpgrade>> UPGRADE = of("upgrade");
	@Unique
	private static final RegistryKey<Registry<ToolMaterial>> TOOL_MATERIALS = of("tool_materials");

	@Unique
	public RegistryKey<Registry<AbstractAbility>> interestingWorld$ability()
	{
		return ABILITY;
	}

	@Unique
	public RegistryKey<Registry<AbstractBoost>> interestingWorld$boost()
	{
		return BOOST;
	}

	@Unique
	public RegistryKey<Registry<AbstractUpgrade>> interestingWorld$upgrade()
	{
		return UPGRADE;
	}

	@Unique
	public RegistryKey<Registry<ToolMaterial>> interestingWorld$toolMaterials()
	{
		return TOOL_MATERIALS;
	}

}
