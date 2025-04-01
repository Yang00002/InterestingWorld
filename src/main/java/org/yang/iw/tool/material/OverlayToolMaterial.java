package org.yang.iw.tool.material;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.tool.part.ToolPart;
import org.yang.iw.tool.part.ToolPartType;

public abstract class OverlayToolMaterial extends ToolMaterial
{
	public final boolean isOverlay()
	{
		return true;
	}

	public OverlayToolMaterial(Identifier identifier)
	{
		super(identifier);
	}

	public OverlayToolMaterial(String id)
	{
		super(id);
	}

	public int materialTake(ToolPart part)
	{
		return Math.max((int) (part.typeBelong().takeMaterialCount * 0.2f), 1);
	}

	@Nullable
	public abstract ToolPart toolPart(ToolPartType part, CommonToolMaterial target);
}
