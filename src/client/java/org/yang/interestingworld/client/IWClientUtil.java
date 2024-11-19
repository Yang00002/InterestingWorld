package org.yang.interestingworld.client;

public class IWClientUtil
{
	public static int rgbToArgb(int rgb)
	{
		return 0xFF000000 | rgb;
	}

	public static int rgbDarkenToArgb(int rgb, float strength)
	{
		int r = rgb >> 16;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		int r2 = (int) (r * strength);
		int g2 = (int) (g * strength);
		int b2 = (int) (g * strength);
		return 0xFF000000 | (r2 << 16) | (g2 << 8) | b2;
	}
}
