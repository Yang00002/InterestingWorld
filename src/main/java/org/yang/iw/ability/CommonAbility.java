package org.yang.iw.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.yang.iw.IWMain;
import org.yang.iw.IWSounds;
import org.yang.iw.api.tag.IntrusiveTag;
import org.yang.iw.item.heart.AbilityHeart;
import org.yang.iw.persistentdata.IWPersistentData;

import static org.yang.iw.util.style.Color.getLevelColor;

public class CommonAbility extends AbstractAbility
{
	private final Text tip = Text.translatable("ability.tip." + id()).withColor(getColor());
	private final Text title = Text.empty().append(Text.literal("「").withColor(getLevelColor(level())))
			.append(Text.translatable("ability.title." + id()).withColor(getColor()))
			.append(Text.literal("」").withColor(getLevelColor(level())));
	private final Text baned_title = Text.empty().append(Text.literal("「").withColor(getLevelColor(level())))
			.append(Text.translatable("ability.title." + id())
					.setStyle(Style.EMPTY.withStrikethrough(true).withColor(getColor())))
			.append(Text.literal("」").withColor(getLevelColor(level())));

	CommonAbility(IntrusiveTag<AbilityHeart> intrusiveTag)
	{
		super(intrusiveTag);
	}

	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public Text getTip()
	{
		return tip;
	}

	@Override
	public boolean canApplyTo(ItemStack stack)
	{
		return true;
	}

	@Override
	public Text getTitleText()
	{
		return title;
	}

	@Override
	public Text getBanedTitleText()
	{
		return baned_title;
	}

	@Override
	public boolean canWork()
	{
		IWPersistentData data = IWMain.getPersistentData();
		return data != null && level() <= data.worldEnergyLevel;
	}

	public void playChargedOverSound(PlayerEntity entity)
	{
		entity.playSound(IWSounds.ABILITY_BAR_FULL, 1.0f, 1.0f);
	}
}
