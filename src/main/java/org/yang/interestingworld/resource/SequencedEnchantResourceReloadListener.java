package org.yang.interestingworld.resource;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.yang.interestingworld.enchant.*;

import static org.yang.interestingworld.util.Base.MOD_ID;
import static org.yang.interestingworld.util.Base.iwlogger;

public class SequencedEnchantResourceReloadListener implements SimpleSynchronousResourceReloadListener
{
	@Override
	public Identifier getFabricId()
	{
		return Identifier.of(MOD_ID, "enchantmentresources");
	}

	@Override
	public void reload(ResourceManager manager)
	{
		handleReload(manager);
	}

	public static void handleReload(ResourceManager manager)
	{
		if (EnchantData.initialized())
		{
			ConflictGroup.reInitialize(manager);
			TableEnchantGroup.reInitialize(manager);
			RandomEnchantGroup.reInitialize(manager);
			ToolGroup.reInitialize(manager);
		}
		else iwlogger.info("Reload: Defer Enchant About data reload because RuneEnchantData hasn't been loaded.");
	}

}
