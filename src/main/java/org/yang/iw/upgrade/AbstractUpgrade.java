package org.yang.iw.upgrade;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.yang.iw.component.IWComponents;
import org.yang.iw.component.UpgradeComponent;
import org.yang.iw.datagen.language.TranslationPool;
import org.yang.iw.util.style.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.yang.iw.util.style.Color.getLevelColor;

public abstract class AbstractUpgrade
{
	private static final AbstractUpgrade DEFAULT = createInstance();
	private final String identifier;
	private final MutableText tip;
	private final MutableText title;

	public AbstractUpgrade(String identifier)
	{
		this.identifier = identifier;
		this.tip = Text.translatable("upgrade.tip." + identifier()).withColor(getColor());
		this.title = Text.empty().append(Text.literal("「").withColor(getLevelColor(level())))
				.append(Text.translatable("upgrade.title." + identifier()).withColor(getColor()))
				.append(Text.literal("」").withColor(getLevelColor(level())));
	}

	private static AbstractUpgrade createInstance()
	{
		return new AbstractUpgrade("empty")
		{
			@Override
			public int getColor()
			{
				return 0;
			}

			@Override
			public boolean canApplyTo(ItemStack stack)
			{
				return false;
			}

			@Override
			protected void applyUpgradeContent(ItemStack toolStack)
			{

			}

			@Override
			public boolean isEmpty()
			{
				return true;
			}

			@Override
			public Map<Item, Integer> getIngredients()
			{
				return new HashMap<>();
			}

			@Override
			public int level()
			{
				return 0;
			}
		};
	}

	public boolean isEmpty()
	{
		return false;
	}

	public static AbstractUpgrade getDefault()
	{
		return DEFAULT;
	}

	public abstract int getColor();

	public String identifier()
	{
		return identifier;
	}

	public MutableText getTitleText()
	{
		return title;
	}

	public Text getTip()
	{
		return tip;
	}

	public abstract boolean canApplyTo(ItemStack stack);

	public void applyUpgrade(ItemStack toolStack)
	{
		applyUpgradeContent(toolStack);
		toolStack.set(IWComponents.UPGRADE, new UpgradeComponent(this));
	}

	protected abstract void applyUpgradeContent(ItemStack toolStack);

	public abstract Map<Item, Integer> getIngredients();

	public abstract int level();

	public void appendIngredientToolTip(Consumer<Text> tooltip)
	{
		var ig = getIngredients();
		if (ig != null)
		{
			tooltip.accept(Text.empty());
			tooltip.accept(Text.translatable(TranslationPool.TOOLTIP_UPGRADE_NEED_BEGIN).withColor(Color.GRAY_RGB));
			for (var i : ig.entrySet())
			{
				var item = i.getKey();
				var count = i.getValue();
				tooltip.accept(
						Text.translatable(item.getTranslationKey()).append(" " + count).withColor(Color.GRAY_RGB));
			}
		}
	}
}
