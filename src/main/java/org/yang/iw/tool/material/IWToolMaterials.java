package org.yang.iw.tool.material;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.yang.iw.IWRegistries;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.api.register.IndependentRegister;
import org.yang.iw.datagen.itemmodel.server.ItemModelDefinition;
import org.yang.iw.datagen.itemmodel.server.ModelParents;
import org.yang.iw.datagen.itemmodel.server.RawItemModel;
import org.yang.iw.datagen.language.TranslationPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@IndependentRegister
public class IWToolMaterials
{
	private static final List<ToolMaterial> MATERIAL_LIST = new ArrayList<>();

	private static ToolMaterial register(ToolMaterial material, String materialText)
	{
		Registry.register(IWRegistries.TOOL_MATERIAL,
				RegistryKey.of(IWRegistryKeys.TOOL_MATERIAL, material.identifier()), material);
		TranslationPool.addString(material.translateKey(), materialText);
		MATERIAL_LIST.add(material);
		return material;
	}

	public static final ToolMaterial WOOD = ToolMaterial.getDefault();

	static
	{
		TranslationPool.addString(WOOD.translateKey(), "木");
		MATERIAL_LIST.add(WOOD);
	}

	public static final ToolMaterial STONE = register(new StoneToolMaterial("stone"), "石");
	public static final ToolMaterial IRON = register(new IronToolMaterial("iron"), "铁");
	public static final ToolMaterial GOLD = register(new GoldToolMaterial("gold"), "金");
	public static final ToolMaterial DIAMOND = register(new DiamondToolMaterial("diamond"), "钻石");
	public static final ToolMaterial NETHERITE = register(new NetheriteToolMaterial("netherite"), "下界合金");

	public static void initialize()
	{
	}

	public static void forEach(Consumer<ToolMaterial> consumer)
	{
		MATERIAL_LIST.forEach(consumer);
	}

	public static Map<ToolMaterial, ItemModelDefinition> buildDefinitionMap(String partName)
	{
		Map<ToolMaterial, ItemModelDefinition> map = new Object2ObjectOpenHashMap<>();
		MATERIAL_LIST.forEach(material -> map.put(material, ItemModelDefinition.of(
				RawItemModel.simple(material.identifier(), ModelParents.HANDHELD).setIdFormat(partName + "/%s")
						.upload())));
		return map;
	}


}
