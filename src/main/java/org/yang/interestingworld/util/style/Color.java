package org.yang.interestingworld.util.style;

public class Color
{
	public static final int BLUE_RGB = 5592575;
	public static final int GREEN_RGB = 5635925;

	public static final int GRAY_RGB = 0XAAAAAA;
	public static final int WHITE_RGB = 0XFFFFFF;
	public static final int PURE_GREEN_RGB = 0X00FF00;
	public static final int DARK_PURPLE_RGB = 11141290;
	public static final int GOLD_RGB = 16755200;
	public static final int RED_RGB = 0xFF5555;
	public static final int DARK_RED_RGB = 0x7f2a2a;
	public static final int PINK_RGB = 0Xff738b;
	public static final int YELLOW_RGB = 0XFFFF55;
	public static final int CYAN_RGB = 0X55FFFF;

	public static int getLevelColor(int lvl)
	{
		return LEVEL_COLOR_MAP[lvl >> 1];
	}

	private static final int[] LEVEL_COLOR_MAP = {WHITE_RGB, GREEN_RGB, BLUE_RGB, DARK_PURPLE_RGB, GOLD_RGB, CYAN_RGB};

	public static int rgbToArgb(int rgb)
	{
		return 0xFF000000 | rgb;
	}

	public static int rgbToDarkenArgb(int rgb, float strength)
	{
		int r = rgb >> 16;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		int r2 = (int) (r * strength);
		int g2 = (int) (g * strength);
		int b2 = (int) (b * strength);
		return 0xFF000000 | (r2 << 16) | (g2 << 8) | b2;
	}
}
