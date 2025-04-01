package org.yang.iw.tool.material;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.datagen.tag.ToolMaterialTagPool;
import org.yang.iw.util.Base;
import org.yang.iw.api.tag.TagInclude;

public class IWToolMaterialTags
{
	public static TagKey<ToolMaterial> WOOD_TYPE = createTag("wood",
			new TagInclude<ToolMaterial>().add(() -> IWToolMaterials.WOOD));

	private static TagKey<ToolMaterial> createTag(String id)
	{
		return TagKey.of(IWRegistryKeys.TOOL_MATERIAL, Identifier.of(Base.MOD_ID, id));
	}

	private static TagKey<ToolMaterial> createTag(String id, TagInclude<ToolMaterial> includes)
	{
		var ret = TagKey.of(IWRegistryKeys.TOOL_MATERIAL, Identifier.of(Base.MOD_ID, id));
		includes.map(item -> ToolMaterialTagPool.add(ret, item), tag -> ToolMaterialTagPool.add(ret, tag));
		return ret;
	}

}
