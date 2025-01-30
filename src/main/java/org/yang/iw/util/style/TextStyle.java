package org.yang.iw.util.style;

import net.minecraft.text.Style;

public class TextStyle
{
	public static String numberToString(float f)
	{
		String s = String.format("%.1f", f);
		if (s.endsWith(".0")) s = String.format("%.0f", f);
		return s;
	}

	public static String numberToString(double f)
	{
		String s = String.format("%.1f", f);
		if (s.endsWith(".0")) s = String.format("%.0f", f);
		return s;
	}


	public static final Style BOLD_STYLE = Style.EMPTY.withBold(true);
}
