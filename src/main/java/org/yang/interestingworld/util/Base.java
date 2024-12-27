package org.yang.interestingworld.util;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Base
{
	public static final String MOD_ID = "interestingworld";
	public static final Logger iwlogger = LogManager.getLogger();
	public static Identifier getIWIdentifier(String string)
	{
		return Identifier.of(MOD_ID, string);
	}
}
