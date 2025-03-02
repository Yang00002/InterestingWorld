package org.yang.iw.api.io;

import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BytesWriter
{
	final ByteArrayOutputStream byteBuffer;

	public BytesWriter(int size)
	{
		byteBuffer = new ByteArrayOutputStream();
	}

	public void writeString(String string) throws IOException
	{
		byteBuffer.write(string.getBytes());
	}

	public void writeIdentifier(Identifier identifier) throws IOException
	{
		byteBuffer.write(identifier.getNamespace().getBytes());
		byteBuffer.write(' ');
		byteBuffer.write(identifier.getPath().getBytes());
	}

	public void writeChar(char c)
	{
		byteBuffer.write(c);
	}

	public void writeInt(int n) throws IOException
	{
		byteBuffer.write(ByteBuffer.allocate(4).putInt(n).array());
	}

	public byte[] toByteArray()
	{
		return byteBuffer.toByteArray();
	}
}
