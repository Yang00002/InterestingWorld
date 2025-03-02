package org.yang.iw.network.bitio;

import io.netty.buffer.ByteBuf;

public class BitReader
{
	public BitReader(ByteBuf buffer)
	{
		this.input = buffer;
	}

	byte buffer = 0;
	ByteBuf input;
	int bitIndex = 8;
	private static final int[] MASK_R = BitWriter.MASK_R;

	public int readUnsignedInt(int maxBitCount)
	{
		int out = 0;
		int outLen = 0;
		while (outLen < maxBitCount)
		{
			if (bitIndex >= 8)
			{
				bitIndex = 0;
				buffer = input.readByte();
			}
			int readSize = Math.min(maxBitCount - outLen, 8 - bitIndex);
			out |= ((buffer & MASK_R[8 - bitIndex]) >> (8 - readSize - bitIndex)) << outLen;
			bitIndex += readSize;
			outLen += readSize;
		}
		return out;
	}

	public boolean readBoolean()
	{
		return readUnsignedInt(1) != 0;
	}

	public float readFloat()
	{
		return Float.intBitsToFloat(readUnsignedInt(32));
	}

	public int readUnsignedInt()
	{
		return readUnsignedInt(32);
	}
}
