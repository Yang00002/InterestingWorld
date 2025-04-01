package org.yang.iw.mixin.mixin;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.yang.iw.ability.AbstractAbility;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.mixin.helper.HelperRegistries;
import org.yang.iw.mixin.mixin_interface.InterfaceRegistries;
import org.yang.iw.tool.material.ToolMaterial;
import org.yang.iw.upgrade.AbstractUpgrade;

@Mixin(Registries.class)
public abstract class MixinRegistries implements InterfaceRegistries
{
	@Shadow
	private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key,
										  Registries.Initializer<T> initializer)
	{
		return null;
	}

	@Shadow
	private static <T> Registry<T> createIntrusive(RegistryKey<? extends Registry<T>> key,
												   Registries.Initializer<T> initializer)
	{
		return null;
	}

	@Unique
	private static final Registry<AbstractAbility> ABILITY = create(new RegistryKeys().interestingWorld$ability(),
			HelperRegistries::registerDefaultAbility);
	@Unique
	private static final Registry<AbstractBoost> BOOST = createIntrusive(new RegistryKeys().interestingWorld$boost(),
			HelperRegistries::registerDefaultBoost);
	@Unique
	private static final Registry<AbstractUpgrade> UPGRADE = create(new RegistryKeys().interestingWorld$upgrade(),
			HelperRegistries::registerDefaultUpgrade);
	@Unique
	private static final Registry<ToolMaterial> TOOL_MATERIALS = createIntrusive(
			new RegistryKeys().interestingWorld$toolMaterials(), HelperRegistries::registerDefaultToolMaterial);

	@Unique
	public Registry<AbstractAbility> interestingWorld$ability()
	{
		return ABILITY;
	}

	@Unique
	public Registry<AbstractBoost> interestingWorld$boost()
	{
		return BOOST;
	}

	@Unique
	public Registry<AbstractUpgrade> interestingWorld$upgrade()
	{
		return UPGRADE;
	}

	@Unique
	public Registry<ToolMaterial> interestingWorld$tool_material()
	{
		return TOOL_MATERIALS;
	}
}
