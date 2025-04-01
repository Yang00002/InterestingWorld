package org.yang.iw.util.style;

import net.minecraft.text.Style;

import java.text.DecimalFormat;

public class TextStyle
{
	private static String[] NUMBER_ROMA_STRING = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI",
												  "XII"};

	public static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("#.####");

	public static String getNumberString(int n)
	{
		if (n > 0 && n < NUMBER_ROMA_STRING.length) return NUMBER_ROMA_STRING[n - 1];
		return String.valueOf(n);
	}

	public static String numberToString(float f)
	{
		String s = String.format("%.1f", f);
		if (s.endsWith(".0")) s = String.format("%.0f", f);
		return s;
	}

	public static String cosineTo2Angle(double cosine)
	{
		return numberToString(Math.toDegrees(Math.acos(cosine)) * 2);
	}

	public static String tickToSecond(int tick)
	{
		return numberToString(tick / 20f);
	}

	public static String numberToString(double f)
	{
		String s = String.format("%.1f", f);
		if (s.endsWith(".0")) s = String.format("%.0f", f);
		return s;
	}


	public static final Style BOLD_STYLE = Style.EMPTY.withBold(true);
	public static final Style UNDERLIEN_STYLE = Style.EMPTY.withUnderline(true);

	public static Style backGroundColored(int argb)
	{
		return Style.EMPTY.withShadowColor(argb);
	}

	public static Style underLinedWithBackGroundColored(int argb)
	{
		return Style.EMPTY.withShadowColor(argb).withUnderline(true);
	}
}
