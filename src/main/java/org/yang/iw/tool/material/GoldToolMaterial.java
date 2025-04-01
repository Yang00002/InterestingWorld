package org.yang.iw.tool.material;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.iw.api.java.PiledShortMap;
import org.yang.iw.boost.AbstractBoost;
import org.yang.iw.boost.IWBoosts;
import org.yang.iw.tool.part.Blade;
import org.yang.iw.tool.part.ShortHandle;
import org.yang.iw.tool.part.ToolPart;
import org.yang.iw.tool.part.ToolPartType;
import org.yang.iw.util.style.Color;

import java.util.List;

public class GoldToolMaterial extends CommonToolMaterial
{

	public GoldToolMaterial(Identifier identifier)
	{
		super(identifier);
	}

	public GoldToolMaterial(String identifier)
	{
		super(identifier);
	}

	@Override
	public int materialColor()
	{
		return 0Xf5cc27;
	}

	@Override
	public void addToolParts()
	{
		toolPartMap.put(ToolPartType.BLADE, new Blade(this,4f, 1.8f, 31, 8, 10, 0.5f));
		toolPartMap.put(ToolPartType.SHORT_HANDLE, new ShortHandle(this,1.0f, 1.2f, 0.8f, 4, 5, 1.0f));
	}

	@Override
	public void addBoost(ToolPart toolPart, PiledShortMap<AbstractBoost> boostMap)
	{
		var type = toolPart.typeBelong();
		switch (type.typeClass)
		{
			case TOP, HANDLE:
				boostMap.add(IWBoosts.REPEAT_ATTACK, 1);
		}
	}

	@Override
	public void appendTooltip(List<Text> tooltip, ToolPart part)
	{
		super.appendTooltip(tooltip, part);
		var type = part.typeBelong();
		switch (type.typeClass)
		{
			case TOP, HANDLE:
				tooltip.add(ToolMaterial.selfTakeBoostText(IWBoosts.REPEAT_ATTACK, 1).withColor(Color.GRAY_RGB));
		}
	}
}
