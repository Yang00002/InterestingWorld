package org.yang.interestingworld;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.block.forgingblock.ForgingBlockScreenHandler;
import org.yang.interestingworld.util.Base;

public class IWScreenHandlers
{
	public static final ScreenHandlerType<ForgingBlockScreenHandler> FORGINGBLOCK_SCREEN_HANDLER = Registry.register(
			Registries.SCREEN_HANDLER, Identifier.of(Base.MOD_ID, "forging_block"),
			new ScreenHandlerType<>(ForgingBlockScreenHandler::new, FeatureSet.empty()));

	public static void initialize()
	{

	}
}
