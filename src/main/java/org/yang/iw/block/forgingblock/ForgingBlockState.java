package org.yang.iw.block.forgingblock;

import net.minecraft.text.Text;
import org.yang.iw.datagen.language.TranslationPool;

public enum ForgingBlockState
{


	/***
	 * 上槽位：空/满耐久工具。下槽位：空。
	 * <p>
	 * - 标题显示通用标题。
	 */
	EMPTY(Text.translatable(TranslationPool.addString("forging_block.title.empty", "修复、强化、升级、锻造和能力修改"))),
	/***
	 * 上槽位：耐久不满工具。下槽位：空。材料：修复原料。
	 * <p>
	 * - 标题显示修复。
	 * <p>
	 * - 修复主要条目，显示代价。
	 */
	REPAIR(Text.translatable(TranslationPool.addString("forging_block.title.repair", "修复")),
			new Text[]{Text.translatable(TranslationPool.addString("forging_block.repair.t1", "修复花费："))},
			new String[]{TranslationPool.addString("forging_block.repair.t2", "%s / %s")}),
	/***
	 * 上槽位：耐久不满工具。下槽位：空。材料：空。（有用于修复的材料）
	 * <p>
	 * - 标题显示修复。
	 * <p>
	 * - 没有原料。
	 */
	REPAIR_LACK_INGREDIENT(REPAIR.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.repair_lack_ingredient", "需要材料以进行修复"))}),

	/***
	 * 上槽位：耐久不满工具。下槽位：空。材料：空。（无用于修复的材料）
	 * <p>
	 * - 标题显示修复。
	 * <p>
	 * - 无法修复。
	 */
	REPAIR_DISABLE(REPAIR.title,
			new Text[]{Text.translatable(TranslationPool.addString("forging_block.repair_disable", "该工具无法修复"))}),

	/***
	 * 上槽位：空。下槽位：基础心。（无强化）
	 * <p>
	 * - 标题显示强化附加。
	 * <p>
	 * - 放入工具以指示强化种类。
	 */
	APPEND_BOOST_NEED_TOOL_HINT(
			Text.translatable(TranslationPool.addString("forging_block.title.append_boost", "强化附加")),
			new Text[]{Text.

							   translatable(TranslationPool.addString("forging_block.append_boost_need_tool_hint",
					"放入工具以指示附加的强化种类"))}),

	APPEND_BOOST_NO_BOOST_TIME(APPEND_BOOST_NEED_TOOL_HINT.title,
			new Text[]{Text.translatable(TranslationPool.TOOLTIP_BOOSTTIME_OUT)}),

	/***
	 * 上槽位：工具。下槽位：基础心。（无强化） 没有可用强化
	 * <p>
	 * - 标题显示强化附加。
	 * <p>
	 * - 没有可用强化。
	 */
	APPEND_BOOST_NO_AVAILABLE(APPEND_BOOST_NEED_TOOL_HINT.title, new Text[]{
			Text.translatable(TranslationPool.addString("forging_block.append_boost_no_available", "无可用强化"))}),

	/***
	 * 上槽位：工具。下槽位：基础心。（无强化） 有可用强化
	 * <p>
	 * - 标题显示强化附加。
	 * <p>
	 * - 强化附加主条目。
	 */
	APPEND_BOOST(APPEND_BOOST_NEED_TOOL_HINT.title),

	/***
	 * 上槽位：空。下槽位：基础心。（有强化）
	 * <p>
	 * - 标题显示强化。
	 * <p>
	 * - 需要工具。
	 */
	BOOST_NEED_TOOL(Text.translatable(TranslationPool.addString("forging_block.title.boost", "强化")), new Text[]{Text.

																														  translatable(
			TranslationPool.addString("forging_block.boost_need_tool", "放入工具以进行强化"))}),

	/***
	 * 上槽位：工具。下槽位：基础心。（有强化）无可用强化
	 * <p>
	 * - 标题显示强化。
	 * <p>
	 * - 没有合适的强化。
	 */
	BOOST_NO_SUITABLE(BOOST_NEED_TOOL.title, new Text[]{
			Text.translatable(TranslationPool.addString("forging_block.boost_no_suitable", "没有适合的强化"))}),

	/***
	 * 上槽位：工具。下槽位：基础心。（有强化）有可用强化，槽位足够
	 * <p>
	 * - 标题显示强化。
	 * <p>
	 * - 强化主条目。
	 */

	BOOST(BOOST_NEED_TOOL.title,
			new Text[]{Text.translatable(TranslationPool.addString("forging_block.boost.t1", "你还不能使用该强化")),
					   Text.translatable(TranslationPool.addString("forging_block.boost.t2", "强化花费："))},
			new String[]{TranslationPool.

								 addString("forging_block.boost.t3", "%s / %s")}),

	/***
	 * 上槽位：工具。下槽位：基础心。（有强化）有可用强化，槽位不够
	 * <p>
	 * - 标题显示强化。
	 * <p>
	 * - 可强化次数不足。
	 */
	BOOST_NO_SLOT(BOOST_NEED_TOOL.title,
			new Text[]{Text.translatable(TranslationPool.addString("forging_block.boost_no_slot", "强化次数不足"))}),

	/***
	 * 上槽位：空。下槽位：能力心。（无能力）
	 * <p>
	 * - 标题显示能力提取。
	 * <p>
	 * - 放入工具。
	 */
	EXTRACT_ABILITY_NO_TOOL(
			Text.translatable(TranslationPool.addString("forging_block.title.extract_ability", "能力提取")),
			new Text[]{Text.

							   translatable(
					TranslationPool.addString("forging_block.extract_ability_no_tool", "放入工具以提取能力"))}),

	/***
	 * 上槽位：工具（无能力）。下槽位：能力心。（无能力）
	 * <p>
	 * - 标题显示能力提取。
	 * <p>
	 * - 无能力。
	 */
	EXTRACT_ABILITY_NO_ABILITY(EXTRACT_ABILITY_NO_TOOL.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.extract_ability_no_ability", "没有能力可以提取"))}),

	/***
	 * 上槽位：工具（有能力）。下槽位：能力心。（无能力）适合
	 * <p>
	 * - 标题显示能力提取。
	 * <p>
	 * - 主条目
	 */
	EXTRACT_ABILITY(EXTRACT_ABILITY_NO_TOOL.title),

	/***
	 * 上槽位：工具（有能力）。下槽位：能力心。（无能力）不适合
	 * <p>
	 * - 标题显示能力提取。
	 * <p>
	 * - 不支持能力。
	 */
	EXTRACT_ABILITY_NOT_SUPPORT(EXTRACT_ABILITY_NO_TOOL.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.extract_ability_no_ability", "这种心无法携物品上的能力"))}),
/////////////////

	/***
	 * 上槽位：空。下槽位：能力心。（有能力）
	 * <p>
	 * - 标题显示能力附加。
	 * <p>
	 * - 放入工具。
	 */
	APPEND_ABILITY_NO_TOOL(
			Text.translatable(TranslationPool.addString("forging_block.title.append_ability", "能力附加")),
			new Text[]{Text.

							   translatable(
					TranslationPool.addString("forging_block.append_ability_no_tool", "放入工具以附加能力"))}),

	/***
	 * 上槽位：工具（无能力）。下槽位：能力心。（有能力）适合
	 * <p>
	 * - 标题显示能力附加。
	 * <p>
	 * - 主条目
	 */
	APPEND_ABILITY(APPEND_ABILITY_NO_TOOL.title),

	/***
	 * 上槽位：工具（无能力）。下槽位：能力心。（有能力）不适合
	 * <p>
	 * - 标题显示能力附加。
	 * <p>
	 * - 能力不合适
	 */
	APPEND_ABILITY_NOT_SUITABLE(APPEND_ABILITY_NO_TOOL.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.append_ability_not_suitable", "能力与工具不兼容"))}),

	/***
	 * 上槽位：工具（有能力）。下槽位：能力心。（有能力）
	 * <p>
	 * - 标题显示能力附加。
	 * <p>
	 * - 已经有能力
	 */
	APPEND_ABILITY_ALREADY_HAVE(APPEND_ABILITY_NO_TOOL.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.append_ability_already_have", "工具已经存在能力"))}),


	UPGRADE_LEVEL_LOW(Text.translatable(TranslationPool.addString("forging_block.title.upgrade", "升级")),
			new Text[]{Text.

							   translatable(
					TranslationPool.addString("forging_block.upgrade_level_low", "你还不能使用该升级"))}),


	UPGRADE_NEED_TOOL(UPGRADE_LEVEL_LOW.title, new Text[]{
			Text.translatable(TranslationPool.addString("forging_block.upgrade_level_low", "放入工具以进行升级"))}),

	/***
	 * 上槽位：工具（无升级）。下槽位：升级模板。（有材料，匹配）
	 * <p>
	 * - 标题显示升级。
	 * <p>
	 * - 主条目
	 */
	UPGRADE(UPGRADE_LEVEL_LOW.title),

	/***
	 * 上槽位：工具（无升级）。下槽位：升级模板。（无材料，匹配）
	 * <p>
	 * - 标题显示升级。
	 * <p>
	 * - 缺少材料
	 */
	UPGRADE_LACK_INGREDIENTS(UPGRADE_LEVEL_LOW.title, new Text[]{
			Text.translatable(TranslationPool.addString("forging_block.upgrade_lack_ingredients", "缺少升级材料"))}),

	/***
	 * 上槽位：工具（无升级）。下槽位：升级模板。（不匹配）
	 * <p>
	 * - 标题显示升级。
	 * <p>
	 * - 不合适
	 */
	UPGRADE_NOT_SUITABLE(UPGRADE_LEVEL_LOW.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.append_ability_not_suitable", "升级与工具不兼容"))}),

	/***
	 * 上槽位：工具（有升级）。下槽位：升级模板。
	 * <p>
	 * - 标题显示升级。
	 * <p>
	 * - 不能重复升级
	 */
	UPGRADE_ALREADY_HAVE(UPGRADE_LEVEL_LOW.title, new Text[]{
			Text.translatable(TranslationPool.addString("forging_block.append_ability_not_suitable", "工具已升级"))}),


	FORGE_NEED_MATERIALS(Text.translatable(TranslationPool.addString("forging_block.title.forge", "锻造")), new Text[]{
			Text.translatable(TranslationPool.addString("forging_block.forge_need_materials", "需要材料以进行锻造"))}),

	FORGE(FORGE_NEED_MATERIALS.title),

	FORGE_NOT_A_MATERIAL(FORGE_NEED_MATERIALS.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.forge_not_a_material", "只有锻造材料可以用于锻造"))}),

	FORGE_MATERIAL_UNSUITABLE(FORGE_NEED_MATERIALS.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.forge_material_unsuitable", "锻造材料不适合此工具"))}),

	FORGE_MATERIAL_NOT_ENOUGH(FORGE_NEED_MATERIALS.title, new Text[]{
			Text.translatable(TranslationPool.addString("forging_block.forge_material_not_enough", "锻造材料不足"))}),

	FORGE_OVERLAY_MATERIAL_NOT_ENOUGH(FORGE_NEED_MATERIALS.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.forge_overlay_material_not_enough", "覆层材料不足"))}),

	FORGE_REFUSE_OVERLAY_ALONE(FORGE_NEED_MATERIALS.title, new Text[]{Text.translatable(
			TranslationPool.addString("forging_block.forge_refuse_overlay_alone", "不能仅使用覆层材料锻造"))});

	public final Text title;
	final Text[] tipStatics;
	final String[] tipDynamics;

	ForgingBlockState(Text title, Text[] tipStatics, String[] tipDynamics)
	{
		this.title = title;
		this.tipStatics = tipStatics;
		this.tipDynamics = tipDynamics;
	}

	ForgingBlockState(Text title, Text[] tipStatics)
	{
		this.title = title;
		this.tipStatics = tipStatics;
		this.tipDynamics = null;
	}

	ForgingBlockState(Text title, String[] tipDynamics)
	{
		this.title = title;
		this.tipStatics = null;
		this.tipDynamics = tipDynamics;
	}

	ForgingBlockState(Text title)
	{
		this.title = title;
		this.tipStatics = null;
		this.tipDynamics = null;
	}
}
