package org.yang.iw.util.style;

public class Color
{
	public static final int CopperColorRGB = 0xff6d40;
	public static final int IronColorRGB = 0xd8d8d8;
	public static final int GoldColorRGB = 0xffff55;
	public static final int DiamondColorRGB = 0X55ffff;
	public static final int EmeraldColorRGB = 0X19ef6a;
	public static final int NetheriteColorRGB = 0X4f484f;
	public static final int EnderiteColorRGB = 0X143c34;
	public static final int VoidalloyColorRGB = 0Xa9fff6;
	public static final int BLUE_RGB = 0X5555ff;
	public static final int GREEN_RGB = 0X55ff55;

	public static final int GRAY_RGB = 0XAAAAAA;
	public static final int WHITE_RGB = 0XFFFFFF;
	public static final int PURE_GREEN_RGB = 0X00FF00;
	public static final int DARK_PURPLE_RGB = 0Xaa00aa;
	public static final int GOLD_RGB = 0Xffaa00;
	public static final int RED_RGB = 0xFF5555;
	public static final int DARK_RED_RGB = 0x7f2a2a;
	public static final int PINK_RGB = 0Xff738b;
	public static final int YELLOW_RGB = 0XFFFF55;
	public static final int CYAN_RGB = 0X55FFFF;

	public static int getLevelColor(int lvl)
	{
		lvl = Math.min(lvl, 10);
		return LEVEL_COLOR_MAP[lvl];
	}

	private static final int[] LEVEL_COLOR_MAP = {0Xffffff, 0X99ff99, 0X4dff4d, 0X5c5cff, 0X8833ff, 0Xaa11aa, 0Xffb300,
												  0Xff2222, 0Xff22ff, 0Xffff33, 0X00ffff};

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
