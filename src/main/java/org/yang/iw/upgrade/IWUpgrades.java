package org.yang.iw.upgrade;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.yang.iw.IWItemGroups;
import org.yang.iw.IWRegistries;
import org.yang.iw.IWRegistryKeys;
import org.yang.iw.api.register.DataGenSupplier;
import org.yang.iw.api.register.DependRegister;
import org.yang.iw.api.register.LoadTime;
import org.yang.iw.datagen.itemmodel.CustomItemModelDefinitionProvider;
import org.yang.iw.datagen.itemmodel.server.ItemModelDefinition;
import org.yang.iw.datagen.itemmodel.server.ModelParents;
import org.yang.iw.datagen.itemmodel.server.RawItemModel;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.item.item_builder.CommonItemBuilder;
import org.yang.iw.item.upgrade.UpgradeTemplate;
import org.yang.iw.upgrade.sweeping.Sweeping2Upgrade;
import org.yang.iw.upgrade.sweeping.Sweeping3Upgrade;
import org.yang.iw.upgrade.sweeping.SweepingUpgrade;
import org.yang.iw.util.Base;
import org.yang.iw.util.constants.Strings;

import java.util.function.Consumer;

import static org.yang.iw.util.style.TextStyle.numberToString;

@DataGenSupplier
@DependRegister(depends = IWItemGroups.class)
public class IWUpgrades
{
	static
	{
		LoadTime.assertTime(LoadTime.Type.ON_INITIALIZE);
		LoadTime.assertLoaded(IWItemGroups.class);
	}

	public static String getItemIdOfUpgrade(AbstractUpgrade upgrade)
	{
		return upgrade.identifier() + "_upgrade_template";
	}

	private static AbstractUpgrade register(AbstractUpgrade upgrade, String upgradeName, String upgradeDetail)
	{
		new CommonItemBuilder(settings -> new UpgradeTemplate(settings, upgrade),
				getItemIdOfUpgrade(upgrade)).setCommonModel(ModelParents.GENERATED)
				.setTranslation(upgradeName + Strings.UPGRADE_TEMPLATE_SUFFIX_NAME)
				.addToItemGroup(IWItemGroups.UPGRADE_GROUP).build();
		Registry.register(IWRegistries.UPGRADE,
				RegistryKey.of(IWRegistryKeys.UPGRADE, Identifier.of(Base.MOD_ID, upgrade.identifier())), upgrade);
		TranslationPool.addString("upgrade.title." + upgrade.identifier(), upgradeName);
		TranslationPool.addString("upgrade.tip." + upgrade.identifier(), upgradeDetail);
		return upgrade;
	}

	private static AbstractUpgrade register(AbstractUpgrade upgrade, String upgradeName, String templateNamePrefix,
											String upgradeDetail)
	{
		new CommonItemBuilder(settings -> new UpgradeTemplate(settings, upgrade),
				getItemIdOfUpgrade(upgrade)).setCommonModel(ModelParents.GENERATED)
				.setTranslation(templateNamePrefix + Strings.UPGRADE_TEMPLATE_SUFFIX_NAME)
				.addToItemGroup(IWItemGroups.UPGRADE_GROUP).build();
		Registry.register(IWRegistries.UPGRADE,
				RegistryKey.of(IWRegistryKeys.UPGRADE, Identifier.of(Base.MOD_ID, upgrade.identifier())), upgrade);
		TranslationPool.addString("upgrade.title." + upgrade.identifier(), upgradeName);
		TranslationPool.addString("upgrade.tip." + upgrade.identifier(), upgradeDetail);
		return upgrade;
	}

	private static AbstractUpgrade register(AbstractUpgrade upgrade, String upgradeName, String upgradeDetail,
											Consumer<CommonItemBuilder> customProcessor)
	{
		var builder = new CommonItemBuilder(settings -> new UpgradeTemplate(settings, upgrade),
				getItemIdOfUpgrade(upgrade)).setCommonModel(ModelParents.GENERATED)
				.setTranslation(upgradeName + Strings.UPGRADE_TEMPLATE_SUFFIX_NAME)
				.addToItemGroup(IWItemGroups.UPGRADE_GROUP);
		customProcessor.accept(builder);
		builder.build();
		Registry.register(IWRegistries.UPGRADE,
				RegistryKey.of(IWRegistryKeys.UPGRADE, Identifier.of(Base.MOD_ID, upgrade.identifier())), upgrade);
		TranslationPool.addString("upgrade.title." + upgrade.identifier(), upgradeName);
		TranslationPool.addString("upgrade.tip." + upgrade.identifier(), upgradeDetail);
		return upgrade;
	}

	private static AbstractUpgrade register(AbstractUpgrade upgrade, String upgradeName, String templateNamePrefix,
											String upgradeDetail,

											Consumer<CommonItemBuilder> customProcessor)
	{
		var builder = new CommonItemBuilder(settings -> new UpgradeTemplate(settings, upgrade),
				getItemIdOfUpgrade(upgrade)).setCommonModel(ModelParents.GENERATED)
				.setTranslation(templateNamePrefix + Strings.UPGRADE_TEMPLATE_SUFFIX_NAME)
				.addToItemGroup(IWItemGroups.UPGRADE_GROUP);
		customProcessor.accept(builder);
		builder.build();
		Registry.register(IWRegistries.UPGRADE,
				RegistryKey.of(IWRegistryKeys.UPGRADE, Identifier.of(Base.MOD_ID, upgrade.identifier())), upgrade);
		TranslationPool.addString("upgrade.title." + upgrade.identifier(), upgradeName);
		TranslationPool.addString("upgrade.tip." + upgrade.identifier(), upgradeDetail);
		return upgrade;
	}


	public static AbstractUpgrade SWEEPING_UPGRADE = register(new SweepingUpgrade("sweeping"), "横扫",
			"使武器可以横扫。");
	public static AbstractUpgrade SWEEPING2_UPGRADE = register(new Sweeping2Upgrade("sweeping2"), "横扫",
			"使武器可以横扫，并增加" + numberToString(Sweeping2Upgrade.SWEEP_RATIO) + "横扫伤害比率。",
			commonItemBuilder -> commonItemBuilder.setModel(item -> new CustomItemModelDefinitionProvider(item,
					ItemModelDefinition.of(
							RawItemModel.of(Identifier.of(Base.MOD_ID, getItemIdOfUpgrade(SWEEPING_UPGRADE)))))));
	public static AbstractUpgrade SWEEPING3_UPGRADE = register(new Sweeping3Upgrade("sweeping3"), "横扫",
			"使武器可以横扫，并增加" + numberToString(Sweeping3Upgrade.SWEEP_RATIO) + "横扫伤害比率。");
	//public static AbstractRuneUpgrade SWEEPING4_UPGRADE = register("横扫","使武器可以横扫，并增加" + numberToString
	// (Sweeping4Upgrade.SWEEP_RATIO) + "横扫伤害比率。",new Sweeping4Upgrade());
	public static AbstractUpgrade HEAVY_UPGRADE = register(new HeavyUpgrade("heavy"), "重",
			"增加" + numberToString(HeavyUpgrade.BASIC_DAMAGE_ADD * 100) + "%基础攻击伤害, 减少" +
			numberToString(HeavyUpgrade.BASIC_SPEED_DOWN * 100) + "%基础攻击速度。");


	public static void initialize()
	{
	}
}
