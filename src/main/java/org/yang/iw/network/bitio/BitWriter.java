package org.yang.iw.network.bitio;

import io.netty.buffer.ByteBuf;

public class BitWriter
{
	public BitWriter(ByteBuf buffer)
	{
		this.output = buffer;
	}

	byte buffer = 0;
	ByteBuf output;
	int bitIndex = 0;
	static final int[] MASK_R = new int[]{0b00000000, 0b00000001, 0b00000011, 0b00000111, 0b00001111, 0b00011111,
										  0b00111111, 0b01111111, 0b11111111};
	static final int[] MASK_L = new int[]{0b00000000, 0b10000000, 0b11000000, 0b11100000, 0b11110000, 0b11111000,
										  0b11111100, 0b11111110, 0b11111111};

	public BitWriter writeUnsignedInt(int value, int maxBitCount)
	{
		while (maxBitCount > 0)
		{
			int writeSize = Math.min(maxBitCount, 8 - bitIndex);
			int writeValue = (value & MASK_R[writeSize]) << (8 - bitIndex - writeSize);
			int originalValue = buffer & MASK_L[bitIndex];
			buffer = (byte) (originalValue | writeValue);
			maxBitCount -= writeSize;
			value >>= writeSize;
			bitIndex += writeSize;
			if (bitIndex >= 8)
			{
				bitIndex = 0;
				output.writeByte(buffer);
			}
		}
		return this;
	}

	public BitWriter writeBoolean(boolean value)
	{
		writeUnsignedInt(value ? 1 : 0, 1);
		return this;
	}

	public void close()
	{
		if (bitIndex > 0)
		{
			bitIndex = 0;
			output.writeByte(buffer);
		}
	}

	public BitWriter writeFloat(float f)
	{
		writeUnsignedInt(Float.floatToIntBits(f), 32);
		return this;
	}

	public BitWriter writeUnsignedInt(int value)
	{
		writeUnsignedInt(value, 32);
		return this;
	}

}
