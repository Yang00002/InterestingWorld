package org.yang.iw.tool.material;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.yang.iw.tool.part.ToolPart;
import org.yang.iw.tool.part.ToolPartType;

import java.util.EnumMap;

public abstract class CommonToolMaterial extends ToolMaterial
{
	protected final EnumMap<ToolPartType, ToolPart> toolPartMap = new EnumMap<>(ToolPartType.class);

	public CommonToolMaterial(Identifier identifier)
	{
		super(identifier);
		addToolParts();
	}

	public final boolean isOverlay()
	{
		return false;
	}

	public int materialTake(ToolPart partT)
	{
		return partT.typeBelong().takeMaterialCount;
	}

	public CommonToolMaterial(String id)
	{
		super(id);
		addToolParts();
	}

	@Nullable
	public ToolPart toolPart(ToolPartType part)
	{
		return toolPartMap.getOrDefault(part, null);
	}

	public abstract void addToolParts();
}
