package org.yang.iw.client.mixin.helper;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

public class HelperDrawContext
{
	private static final TooltipComponent EMPTY = TooltipComponent.of(OrderedText.empty());

	private static class OrderedTextToTextVisitor implements CharacterVisitor
	{
		private final MutableText text = Text.empty();

		@Override
		public boolean accept(int index, Style style, int codePoint)
		{
			String car = new String(Character.toChars(codePoint));
			text.append(Text.literal(car).setStyle(style));
			return true;
		}

		public static Text get(OrderedText text)
		{
			OrderedTextToTextVisitor visitor = new OrderedTextToTextVisitor();
			text.accept(visitor);
			return visitor.text;
		}
	}

	public static List<TooltipComponent> fixTooltip(List<TooltipComponent> components, TextRenderer textRenderer,
													int x, int width)
	{
		List<TooltipComponent> fixedComponents = new ArrayList<>();
		int maxWidth = Math.max(32, Math.max(width - 20 - x, x - 28)) & (~0b011111);
		int stateN = 0;
		int stateE = 0;
		for (var commonComponent : components)
		{
			if (commonComponent instanceof OrderedTextTooltipComponent orderedTextTooltipComponent)
			{
				Text textLine = OrderedTextToTextVisitor.get(orderedTextTooltipComponent.getText());
				List<Text> childrenTexts = textLine.getSiblings();
				boolean nEmpty = !childrenTexts.isEmpty();
				if (nEmpty)
				{
					if (stateE == 1)
					{
						stateE = 0;
						fixedComponents.add(TooltipComponent.of(Text.empty().asOrderedText()));
					}
				}
				else stateE = 1;
				MutableText handledTexts = Text.literal("");
				Text lastText = null;
				for (var text : childrenTexts)
				{
					switch (stateN)
					{
						case 0 ->
						{
							if (text.getString().equals("\\"))
							{
								lastText = text;
								stateN = 1;
							}
							else handledTexts.append(text);
						}
						case 1 ->
						{
							if (text.getString().equals("n"))
							{
								List<TooltipComponent> wrappedTexts = textRenderer.wrapLines(handledTexts, maxWidth)
										.stream().map(TooltipComponent::of).toList();
								fixedComponents.addAll(wrappedTexts);
								handledTexts = Text.literal("");
							}
							else handledTexts.append(text);
							stateN = 0;
						}
					}
				}
				if (nEmpty)
				{
					if (stateN == 1) handledTexts.append(lastText);
					List<TooltipComponent> wrappedTexts = textRenderer.wrapLines(handledTexts, maxWidth).stream()
							.map(TooltipComponent::of).toList();
					fixedComponents.addAll(wrappedTexts);
				}
			}
			else
			{
				if (stateE == 1)
				{
					stateE = 0;
					fixedComponents.add(TooltipComponent.of(Text.empty().asOrderedText()));
				}
				fixedComponents.add(commonComponent);
			}
		}
		return fixedComponents;
	}
}
