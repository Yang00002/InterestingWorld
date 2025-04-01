package org.yang.iw.tool.material;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.yang.iw.api.util.MutableAttributeValueDetail;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.tool.part.*;

import java.util.List;
import java.util.Map;

public class WoodToolMaterial extends CommonToolMaterial
{

	public WoodToolMaterial(Identifier identifier)
	{
		super(identifier);
	}

	public WoodToolMaterial(String identifier)
	{
		super(identifier);
	}

	@Override
	public int materialColor()
	{
		return 0Xffbf47;
	}

	@Override
	public void modifyRepairNeed(Map<ToolMaterial, MutableAttributeValueDetail> need, ToolPart part)
	{
		need.forEach((material, attr) -> {
			if (material.isIn(IWToolMaterialTags.WOOD_TYPE)) attr.sum -= 0.5f;
		});
	}

	public static Text HALF_SAVE = TranslationPool.translatable("iw.tool.material.wood_tool_material.half_save",
			"减少修补时50%木类材料消耗", t -> t.withColor(0Xffbf47));
	public static Text DO_NOT_NEED = TranslationPool.translatable("iw.tool.material.wood_tool_material.do_not_need",
			"该部件修补时不消耗材料", t -> t.withColor(0Xffbf47));

	@Override
	public void appendTooltip(List<Text> tooltip, ToolPart part)
	{
		super.appendTooltip(tooltip, part);
		tooltip.add(HALF_SAVE);
		if (part.typeBelong().typeClass == ToolPartTypeClass.HANDLE) tooltip.add(DO_NOT_NEED);
	}


	@Override
	public void addToolParts()
	{
		toolPartMap.put(ToolPartType.BLADE, new Blade(this, 4f, 1.6f, 63, 2, 4, 0.25f));
		toolPartMap.put(ToolPartType.SHORT_HANDLE, new ShortHandle(this, 1.0f, 1.0f, 1.0f, 1, 1, 0.8f));
	}
}
