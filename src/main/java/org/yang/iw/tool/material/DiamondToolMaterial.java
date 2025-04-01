package org.yang.iw.tool.material;

import net.minecraft.util.Identifier;
import org.yang.iw.tool.part.Blade;
import org.yang.iw.tool.part.ShortHandle;
import org.yang.iw.tool.part.ToolPartType;

public class DiamondToolMaterial extends CommonToolMaterial
{
	public DiamondToolMaterial(Identifier identifier)
	{
		super(identifier);
	}

	public DiamondToolMaterial(String identifier)
	{
		super(identifier);
	}

	@Override
	public int materialColor()
	{
		return 0X55ffff;
	}

	@Override
	public void addToolParts()
	{
		toolPartMap.put(ToolPartType.BLADE, new Blade(this,7f, 1.6f, 512, 5, 5, 0.35f));
		toolPartMap.put(ToolPartType.SHORT_HANDLE, new ShortHandle(this,1.0f, 1.0f, 1.2f, 3, 2, 0.8f));
	}
}
