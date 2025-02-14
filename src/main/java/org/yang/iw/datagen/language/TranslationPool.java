package org.yang.iw.datagen.language;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import org.yang.iw.util.constants.Strings;

import java.util.HashMap;
import java.util.Map;

public class TranslationPool
{
	static Map<String, String> providers = new HashMap<>();

	public static void generatePool(FabricLanguageProvider.TranslationBuilder translationBuilder)
	{
		providers.forEach(translationBuilder::add);
	}

	public static void clearPool()
	{
		providers = null;
	}

	public static void addItem(Item it, String trans)
	{
		providers.put(it.getTranslationKey(), trans);
	}

	public static void addBlock(Block it, String trans)
	{
		providers.put(it.getTranslationKey(), trans);
	}

	public static <T extends Entity> void addEntity(EntityType<T> it, String trans)
	{
		providers.put(it.getTranslationKey(), trans);
	}

	public static void addStatusEffect(StatusEffect it, String trans)
	{
		providers.put(it.getTranslationKey(), trans);
	}

	public static void addString(String ori, String trans)
	{
		providers.put(ori, trans);
	}

	private static String add(String ori, String trans)
	{
		providers.put(ori, trans);
		return ori;
	}

	public static final String IW_KEYBINDING_CATAGORY_N = add("iw.key.category", "InterestingWorld 按键绑定");
	public static final String ABILITY_OPEN_CLOSE_KEYBIND_N = add("iw.key.ability_oc", "开关主手武器能力");
	public static final String TOOLTIP_UPGRADE_TEMPLATE_LEVEL_N = add("tooltip.upgradeTemplate.level", "升级等级：");
	public static final String UPGRADE_TEMPLATE_NAME_SUFFIX_N = add("suffix.upgradeTemplate",
			Strings.UPGRADE_TEMPLATE_SUFFIX_NAME);
	public static final String FORGING_BLOCK_TITLE_ALL_N = add("forgingblock.title.all", "修复、附魔、升级和能力修改");
	public static final String FORGING_BLOCK_TITLE_PREENCHANT_N = add("forgingblock.title.preenchant", "附魔打磨");
	public static final String FORGING_BLOCK_TIP_PREENCHANT_N = add("forgingblock.text.preenchant.2",
			"放入工具以指示附魔种类");
	public static final String FORGING_BLOCK_TITLE_ENCHANT_N = add("forgingblock.title.enchant", "附魔");
	public static final String FORGING_BLOCK_TIP_CANNOT_USE_BECAUSE_LEVEL_N = add("forgingblock.text.level",
			"你还不能使用该符文");

	public static final String FORGING_BLOCK_TIP_CANNOT_USE_BECAUSE_OUTPUT_LEVEL_N = add("forgingblock.text.outlevel",
			"对于你得到的附魔而言，世界等级太低");
	public static final String FORGING_BLOCK_TIP_ENCHANT_COST_L_N = add("forgingblock.text.enchant.3xpl", "附魔花费：");
	public static final String FORGING_BLOCK_TIP_ENCHANT_COST_R_N = add("forgingblock.text.enchant.3xpr", "%s / %s");
	public static final String FORGING_BLOCK_TIP_ENCHANT_NO_USEFUL_N = add("forgingblock.text.enchant.4",
			"没有可用的附魔");
	public static final String FORGING_BLOCK_TIP_ENCHANT_PUT_TOOL_N = add("forgingblock.text.enchant.5",
			"放入工具以进行附魔");
	public static final String FORGING_BLOCK_TITLE_REPAIR_N = add("forgingblock.title.repair", "修复");
	public static final String FORGING_BLOCK_TIP_REPAIR_NEED_INGREDIENT_N = add("forgingblock.text.repair.6",
			"需要原料以进行修复");
	public static final String FORGING_BLOCK_TIP_REPAIR_COST_L_N = add("forgingblock.text.repair.7l", "修复花费：");
	public static final String FORGING_BLOCK_TIP_REPAIR_COST_R_N = add("forgingblock.text.repair.7r",
			"%s / %s 材料 %s");
	public static final String FORGING_BLOCK_TITLE_ABILITY_ADD_N = add("forgingblock.title.abilityadd", "能力附加");
	public static final String FORGING_BLOCK_TIP_ABILITY_ADD_INCOMPATIBLE_N = add("forgingblock.text.abilityadd.9",
			"能力和工具不兼容");
	public static final String FORGING_BLOCK_TIP_ABILITY_ADD_ALREADY_HAVE_N = add("forgingblock.text.abilityadd.10",
			"工具已经存在能力");
	public static final String FORGING_BLOCK_TIP_ABILITY_ADD_PUT_TOOL_N = add("forgingblock.text.abilityadd.11",
			"放入工具以附加能力");
	public static final String FORGING_BLOCK_TITLE_ABILITY_REMOVE_N = add("forgingblock.title.abilityremove",
			"能力提取");
	public static final String FORGING_BLOCK_TIP_ABILITY_REMOVE_TOOL_BREAK_N = add(
			"forgingblock.text.abilityremove" + ".12", "工具损坏概率：%s%%");

	public static final String FORGING_BLOCK_TIP_ABILITY_REMOVE_NO_ABILITY_N = add(
			"forgingblock.text.abilityremove" + ".13", "没有可提取的能力");
	public static final String FORGING_BLOCK_TIP_ABILITY_REMOVE_PUT_TOOL_N = add("forgingblock.text.abilityremove.14",
			"放入工具以提取能力");
	public static final String FORGING_BLOCK_TITLE_UPGRADE_N = add("forgingblock.title.upgrade", "升级");
	public static final String FORGING_BLOCK_TIP_UPGRADE_LACK_INGREDIENT_N = add("forgingblock.text.upgrade.17",
			"缺少原料");
	public static final String FORGING_BLOCK_TIP_UPGRADE_INCOMPATIBLE_N = add("forgingblock.text.upgrade.18",
			"升级和工具不兼容");
	public static final String FORGING_BLOCK_TIP_UPGRADE_REPEAT_N = add("forgingblock.text.upgrade.19", "不能重复升级");
	public static final String FORGING_BLOCK_TIP_UPGRADE_PUT_TOOL_N = add("forgingblock.text.upgrade.20",
			"放入工具以进行升级");
	public static final String FORGING_BLOCK_TIP_ABILITY_REMOVE_INCOMPATIBLE_N = add(
			"forgingblock.text.abilityremove.21", "这种心无法携物品上的能力");
	public static final String FORGING_BLOCK_TIP_ABILITY_REMOVE_LEVEL_N = add("forgingblock.text.abilityremove.22",
			"这种心材料太差, 无法提取能力");

	public static final String TOOLTIP_ENERGYTOOL_COMMON_ENERGY = add("tooltip.energy.count", "储存能量");

	public static final String TOOLTIP_UPGRADE_NEED_BEGIN = add("upgrade.need.txet", "需求：");

	public static final String TOOLTIP_BAN_ABILITY_BEGIN = add("banedabilitydetail", "你还不能使用此能力。");

	public static final String TOOLTIP_DEFAULT_ENCHANT = add("tooltip.defaultenchantment", "自带：");
	public static final String COMMAND_SET_WORLD_LEVEL = add("iw.worldlevel_set_success", "世界等级已设置为 %s。");
	public static final String COMMAND_SET_WORLD_LEVEL_FAIL = add("iw.worldlevel_set_fail",
			"无法将世界等级设置为 %s，只能设置为一个非负数。");

	public static final String TOOLTIP_HEART_CONTAINER_LEVEL = add("tooltip.heart.containerLevel", "材料等级：");
	public static final String TOOLTIP_HEART_CAN_ENCHANT = add("tooltip.heart.canEnchant", "可附魔");
	public static final String TOOLTIP_HEART_CAN_TAKE_ABILITY = add("tooltip.heart.canHaveAbility", "可携带能力");
	public static final String TOOLTIP_HEART_ABILITY_LEVEL = add("tooltip.heart.enchantLevel", "能力等级：");
	public static final String TOOLTIP_HEART_ENCHANT_LEVEL = add("tooltip.heart.abilityLevel", "附魔等级：");
	public static final String TOOLTIP_HEART_NEED_FURTHER_ENCHANT = add("tooltip.heart.furtherEnchant", "它还需要打磨");
	public static final String ENCHANT_HEART = add("item.iw.enchantedheart", "附魔之心");
	public static final String COMMAND_NO_STACK_IN_HAND = add("command.nostackinhand", "%s未手持任何物品");
	public static final String COMMAND_NOT_A_ENCHANT_HEART = add("command.noaheart", "%s手持物品不是可附魔的心类物品");
	public static final String COMMAND_SET_TEST_NUMBER_SUCCESS = add("command.settestnumber.s", "成功将数字%s设置为%s");
	public static final String COMMAND_NOT_A_PLAYER = add("command.noaplayer", "执行者不是玩家");

}
