package org.yang.iw.tool.material;

import net.minecraft.util.Identifier;
import org.yang.iw.tool.part.Blade;
import org.yang.iw.tool.part.ShortHandle;
import org.yang.iw.tool.part.ToolPartType;

public class StoneToolMaterial extends CommonToolMaterial
{
	public StoneToolMaterial(Identifier identifier)
	{
		super(identifier);
	}

	public StoneToolMaterial(String identifier)
	{
		super(identifier);
	}

	@Override
	public int materialColor()
	{
		return 0X999999;
	}

	@Override
	public void addToolParts()
	{
		toolPartMap.put(ToolPartType.BLADE, new Blade(this,5f, 1.6f, 127, 2, 4, 0.2f));
		toolPartMap.put(ToolPartType.SHORT_HANDLE, new ShortHandle(this,1.0f, 1.0f, 0.9f, 0, 0, 0.75f));
	}
}
