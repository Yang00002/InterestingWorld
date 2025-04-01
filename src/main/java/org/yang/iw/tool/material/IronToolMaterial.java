package org.yang.iw.tool.material;

import net.minecraft.util.Identifier;
import org.yang.iw.tool.part.Blade;
import org.yang.iw.tool.part.ShortHandle;
import org.yang.iw.tool.part.ToolPartType;

public class IronToolMaterial extends CommonToolMaterial
{
	public IronToolMaterial(Identifier identifier)
	{
		super(identifier);
	}

	public IronToolMaterial(String identifier)
	{
		super(identifier);
	}

	@Override
	public int materialColor()
	{
		return 0Xdddddd;
	}

	@Override
	public void addToolParts()
	{
		toolPartMap.put(ToolPartType.BLADE, new Blade(this,6f, 1.6f, 255, 4, 6, 0.4f));
		toolPartMap.put(ToolPartType.SHORT_HANDLE, new ShortHandle(this,1.0f, 1.0f, 1.1f, 2, 2, 0.9f));
	}
}
