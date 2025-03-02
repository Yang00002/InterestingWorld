package org.yang.iw.api.io;

import net.minecraft.util.Identifier;

import java.nio.ByteBuffer;

public class BytesReader
{
	final ByteBuffer byteBuffer;

	public BytesReader(byte[] bytes)
	{
		byteBuffer = ByteBuffer.wrap(bytes);
	}

	public String readStringTillSpace()
	{
		StringBuilder result = new StringBuilder();
		while (byteBuffer.hasRemaining())
		{
			byte b = byteBuffer.get();
			if (b == ' ') break;
			result.append((char) b);
		}
		return result.toString();
	}

	public Identifier readIdentifier()
	{
		String namespace = readStringTillSpace();
		String id = readStringTillSpace();
		return Identifier.of(namespace, id);
	}
}
