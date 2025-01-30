package org.yang.iw.rune_upgrade;

import net.minecraft.data.client.Models;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.yang.iw.IWItemGroups;
import org.yang.iw.datagen.itemmodel.LocatedItemModelProvider;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.item.item_builder.CommonItemBuilder;
import org.yang.iw.item.upgrade.UpgradeTemplate;
import org.yang.iw.rune_upgrade.sweeping.Sweeping2Upgrade;
import org.yang.iw.rune_upgrade.sweeping.Sweeping3Upgrade;
import org.yang.iw.rune_upgrade.sweeping.SweepingUpgrade;
import org.yang.iw.util.Base;
import org.yang.iw.util.constants.Strings;

import java.util.function.Consumer;

import static org.yang.iw.util.style.TextStyle.numberToString;

public class IWRuneUpgrades
{

	public static String getItemIdOfUpgrade(AbstractRuneUpgrade upgrade)
	{
		return upgrade.id() + "_upgrade_template";
	}

	private static AbstractRuneUpgrade register(String upgradeName, String upgradeDetail, AbstractRuneUpgrade upgrade)
	{
		new CommonItemBuilder(settings -> new UpgradeTemplate(settings, upgrade),
				getItemIdOfUpgrade(upgrade)).setCommonModel(Models.GENERATED)
				.setTranslation(upgradeName + Strings.UPGRADE_TEMPLATE_SUFFIX_NAME)
				.addToItemGroup(IWItemGroups.UPGRADE_GROUP).build();
		TranslationPool.addString("upgrade.title." + upgrade.id(), upgradeName);
		TranslationPool.addString("upgrade.tip." + upgrade.id(), upgradeDetail);
		return upgrade;
	}

	private static AbstractRuneUpgrade register(String upgradeName, String templateNamePrefix, String upgradeDetail,
												AbstractRuneUpgrade upgrade)
	{
		new CommonItemBuilder(settings -> new UpgradeTemplate(settings, upgrade),
				getItemIdOfUpgrade(upgrade)).setCommonModel(Models.GENERATED)
				.setTranslation(templateNamePrefix + Strings.UPGRADE_TEMPLATE_SUFFIX_NAME)
				.addToItemGroup(IWItemGroups.UPGRADE_GROUP).build();
		TranslationPool.addString("upgrade.title." + upgrade.id(), upgradeName);
		TranslationPool.addString("upgrade.tip." + upgrade.id(), upgradeDetail);
		return upgrade;
	}

	private static AbstractRuneUpgrade register(String upgradeName, String upgradeDetail, AbstractRuneUpgrade upgrade,
												Consumer<CommonItemBuilder> customProcessor)
	{
		var builder = new CommonItemBuilder(settings -> new UpgradeTemplate(settings, upgrade),
				getItemIdOfUpgrade(upgrade)).setCommonModel(Models.GENERATED)
				.setTranslation(upgradeName + Strings.UPGRADE_TEMPLATE_SUFFIX_NAME)
				.addToItemGroup(IWItemGroups.UPGRADE_GROUP);
		customProcessor.accept(builder);
		builder.build();
		TranslationPool.addString("upgrade.title." + upgrade.id(), upgradeName);
		TranslationPool.addString("upgrade.tip." + upgrade.id(), upgradeDetail);
		return upgrade;
	}

	private static AbstractRuneUpgrade register(String upgradeName, String templateNamePrefix, String upgradeDetail,
												AbstractRuneUpgrade upgrade,
												Consumer<CommonItemBuilder> customProcessor)
	{
		var builder = new CommonItemBuilder(settings -> new UpgradeTemplate(settings, upgrade),
				getItemIdOfUpgrade(upgrade)).setCommonModel(Models.GENERATED)
				.setTranslation(templateNamePrefix + Strings.UPGRADE_TEMPLATE_SUFFIX_NAME)
				.addToItemGroup(IWItemGroups.UPGRADE_GROUP);
		customProcessor.accept(builder);
		builder.build();
		TranslationPool.addString("upgrade.title." + upgrade.id(), upgradeName);
		TranslationPool.addString("upgrade.tip." + upgrade.id(), upgradeDetail);
		return upgrade;
	}


	public static AbstractRuneUpgrade SWEEPING_UPGRADE = register("横扫", "使武器可以横扫。", new SweepingUpgrade());
	public static AbstractRuneUpgrade SWEEPING2_UPGRADE = register("横扫",
			"使武器可以横扫，并增加" + numberToString(Sweeping2Upgrade.SWEEP_RATIO) + "横扫伤害比率。",
			new Sweeping2Upgrade(), commonItemBuilder -> commonItemBuilder.setModel(
					item -> new LocatedItemModelProvider(item, Models.GENERATED).setTextureAItemTexture(
							Registries.ITEM.get(Identifier.of(Base.MOD_ID, getItemIdOfUpgrade(SWEEPING_UPGRADE))))));
	public static AbstractRuneUpgrade SWEEPING3_UPGRADE = register("横扫",
			"使武器可以横扫，并增加" + numberToString(Sweeping3Upgrade.SWEEP_RATIO) + "横扫伤害比率。",
			new Sweeping3Upgrade());
	//public static AbstractRuneUpgrade SWEEPING4_UPGRADE = register("横扫","使武器可以横扫，并增加" + numberToString
	// (Sweeping4Upgrade.SWEEP_RATIO) + "横扫伤害比率。",new Sweeping4Upgrade());
	public static AbstractRuneUpgrade HEAVY_UPGRADE = register("重",
			"增加" + numberToString(HeavyUpgrade.BASIC_DAMAGE_ADD * 100) + "%基础攻击伤害, 减少" +
			numberToString(HeavyUpgrade.BASIC_SPEED_DOWN * 100) + "%基础攻击速度。", new HeavyUpgrade());

	public static void initialize()
	{
	}
}
