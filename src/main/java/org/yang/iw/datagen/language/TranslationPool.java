package org.yang.iw.datagen.language;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class TranslationPool
{
	static Map<String, Supplier<String>> providers = new HashMap<>();

	public static void generatePool(FabricLanguageProvider.TranslationBuilder translationBuilder)
	{
		if (providers != null) providers.forEach((i, j) -> translationBuilder.add(i, j.get()));
	}

	public static void clearPool()
	{
		providers = null;
	}

	public static void addItem(Item it, String trans)
	{
		if (providers != null) providers.put(it.getTranslationKey(), () -> trans);
	}

	public static void addBlock(Block it, String trans)
	{
		if (providers != null) providers.put(it.getTranslationKey(), () -> trans);
	}

	public static <T extends Entity> void addEntity(EntityType<T> it, String trans)
	{
		if (providers != null) providers.put(it.getTranslationKey(), () -> trans);
	}

	public static void addStatusEffect(StatusEffect it, String trans)
	{
		if (providers != null) providers.put(it.getTranslationKey(), () -> trans);
	}

	public static String addString(String ori, String trans)
	{
		if (providers != null) providers.put(ori, () -> trans);
		return ori;
	}

	public static Text translatable(String ori, String trans)
	{
		if (providers != null) providers.put(ori, () -> trans);
		return Text.translatable(ori);
	}

	public static Text translatable(String ori, String trans, Function<MutableText, Text> func)
	{
		if (providers != null) providers.put(ori, () -> trans);
		return func.apply(Text.translatable(ori));
	}

	public static String addString(String ori, Supplier<String> trans)
	{
		if (providers != null) providers.put(ori, trans);
		return ori;
	}

	private static String add(String ori, String trans)
	{
		if (providers != null) providers.put(ori, () -> trans);
		return ori;
	}

	public static final String IW_KEYBINDING_CATEGORY_N = add("iw.key.category", "InterestingWorld 按键绑定");
	public static final String ABILITY_OPEN_CLOSE_KEYBINDING_N = add("iw.key.ability_oc", "开关主手武器能力");
	public static final String TOOLTIP_UPGRADE_TEMPLATE_LEVEL_N = add("tooltip.upgradeTemplate.level", "升级等级：");
	public static final String FORGING_BLOCK_TIP_ENCHANT_NO_USEFUL_N = add("forgingblock.text.enchant.4",
			"没有可用的强化");
	public static final String TOOLTIP_ENERGYTOOL_COMMON_ENERGY = add("tooltip.energy.count", "储存能量");
	public static final String TOOLTIP_ENERGYTOOL_ENERGY_RATE = add("tooltip.energy.rate", "恢复速率");
	public static final String TOOLTIP_UPGRADE_NEED_BEGIN = add("upgrade.need.txet", "需求：");
	public static final String TOOLTIP_DEFAULT_ENCHANT = add("tooltip.defaultenchantment", "自带：");
	public static final String COMMAND_SET_WORLD_LEVEL = add("iw.worldlevel_set_success", "世界等级已设置为 %s。");
	public static final String COMMAND_SET_WORLD_LEVEL_FAIL = add("iw.worldlevel_set_fail",
			"无法将世界等级设置为 %s，只能设置为一个非负数。");

	public static final String TOOLTIP_HEART_CONTAINER_LEVEL = add("tooltip.heart.containerLevel", "材料等级：");
	public static final String TOOLTIP_HEART_CAN_ATTACH_BOOST = add("tooltip.heart.canBoost", "可附加强化或附魔");
	public static final String TOOLTIP_BOOSTTIME_OUT = add("tooltip.heart.boostOut", "强化次数用尽");
	public static final String TOOLTIP_REMAIN_BOOSTTIME = add("tooltip.heart.remainBoostTime", "剩余强化次数：");

	public static final String TOOLTIP_HEART_CAN_TAKE_ABILITY = add("tooltip.heart.canHaveAbility", "可携带能力");
	public static final String TOOLTIP_HEART_ABILITY_LEVEL = add("tooltip.heart.enchantLevel", "能力等级：");
	public static final String TOOLTIP_HEART_ENCHANT_CONVERT = add("tooltip.heart.convertEnchant",
			"在锻造台将附魔转化为强化");
	public static final String TOOLTIP_HEART_BOOST = add("tooltip.heart.boost", "在锻造台使用以强化工具");
	public static final String COMMAND_NO_STACK_IN_HAND = add("command.nostackinhand", "%s未手持任何物品");
	public static final String COMMAND_NOT_A_ENCHANT_HEART = add("command.noaheart", "%s手持物品不是可附魔的心类物品");
	public static final String COMMAND_SET_TEST_NUMBER_SUCCESS = add("command.settestnumber.s", "成功将数字%s设置为%s");
	public static final String COMMAND_NOT_A_PLAYER = add("command.noaplayer", "执行者不是玩家");
	public static final String TOOLTIP_PROVIDE_MATERIAL_1 = add("tooltip.provide.material1", "材料");
	public static final String TOOLTIP_PROVIDE_MATERIAL_B = add("tooltip.provide.material_b", "块");
	public static final String TOOLTIP_PROVIDE_MATERIAL_I = add("tooltip.provide.material_i", "锭");
	public static final String TOOLTIP_PROVIDE_MATERIAL_N = add("tooltip.provide.material_n", "粒");
	public static final String TOOLTIP_PROVIDE_MATERIAL_2 = add("tooltip.provide.material2", "单位");
	public static final String TOOLTIP_MATERIAL_NEED = add("tooltip.material_need", "需要");
	public static final String TOOLTIP_MATERIAL_WRONG = add("tooltip.material_wrong", "材料不适用");
	public static final String TOOL_PART_DAMAGE = add("tooltip.tool_part.damage", "攻击伤害：");
	public static final String TOOL_PART_SPEED = add("tooltip.tool_part.speed", "攻击速度：");
	public static final String TOOL_PART_DURABILITY = add("tooltip.tool_part.durability", "耐久：");
	public static final String TOOL_PART_BOOST_TIME = add("tooltip.tool_part.boost_time", "强化次数：");
	public static final String TOOL_PART_BOOST_MAX_ENERGY = add("tooltip.tool_part.max_energy", "最大能量：");
	public static final String TOOL_PART_BOOST_ENERGY_RENGEN_RATE = add("tooltip.tool_part.energy_regen_rate",
			"能量恢复速率：");
	public static final String TOOL_PART_PROOF_FIRE = add("tooltip.tool_part.proof_fire", "防火");

}
