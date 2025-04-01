package org.yang.iw.resource;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.api.register.LoadTime;

@IndependentRegister
public class IWResources
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new MaterialProvider());
	}

	public static void initialize()
	{
	}
}
