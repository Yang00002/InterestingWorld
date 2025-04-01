package org.yang.iw.tool.part;

import net.minecraft.util.Identifier;
import org.yang.iw.util.Base;

public enum ToolPartType
{
	EMPTY("empty", 0, ToolPartTypeClass.OTHER),

	BLADE("blade", 1800, ToolPartTypeClass.TOP),

	SHORT_HANDLE("short_handle", 450, ToolPartTypeClass.HANDLE);

	public final int takeMaterialCount;
	public final Identifier emptyTexturePath;
	public final ToolPartTypeClass typeClass;

	ToolPartType(String id, int takeMaterialCount, ToolPartTypeClass typeClass)
	{
		this.takeMaterialCount = takeMaterialCount;
		this.emptyTexturePath = Identifier.of(Base.MOD_ID,
				"textures/gui/container/forging_block/%s.png".formatted(id));
		this.typeClass = typeClass;
	}
}
