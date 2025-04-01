package org.yang.iw.item.forge_template;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.yang.iw.item.IWItems;
import org.yang.iw.tool.ToolBuilder;
import org.yang.iw.tool.material.ToolMaterial;
import org.yang.iw.tool.part.ToolPart;

import static org.yang.iw.tool.part.ToolPartType.BLADE;
import static org.yang.iw.tool.part.ToolPartType.SHORT_HANDLE;

public class SwordForgeTemplateItem extends ForgeTemplateItem
{
	public SwordForgeTemplateItem(Settings settings)
	{
		super(settings, BLADE, SHORT_HANDLE);
	}

	@Override
	public Item itemBelong()
	{
		return IWItems.SWORD;
	}
}
