// SPDX-License-Identifier: LGPL-2.1-or-later
// Copyright (c) 2012-2014 Monty Program Ab
// Copyright (c) 2015-2021 MariaDB Corporation Ab
// Copyright (c) 2021 SingleStore, Inc.

package com.singlestore.jdbc.codec.list;

import com.singlestore.jdbc.client.ReadableByteBuf;
import com.singlestore.jdbc.client.context.Context;
import com.singlestore.jdbc.client.socket.PacketWriter;
import com.singlestore.jdbc.codec.Codec;
import com.singlestore.jdbc.codec.DataType;
import com.singlestore.jdbc.message.server.ColumnDefinitionPacket;
import java.io.IOException;
import java.util.BitSet;
import java.util.Calendar;

public class BitSetCodec implements Codec<BitSet> {

  public static final BitSetCodec INSTANCE = new BitSetCodec();

  public static BitSet parseBit(ReadableByteBuf buf, int length) {
    byte[] arr = new byte[length];
    buf.readBytes(arr);
    revertOrder(arr);
    return BitSet.valueOf(arr);
  }

  public static void revertOrder(byte[] array) {
    int i = 0;
    int j = array.length - 1;
    byte tmp;
    while (j > i) {
      tmp = array[j];
      array[j] = array[i];
      array[i] = tmp;
      j--;
      i++;
    }
  }

  public String className() {
    return BitSet.class.getName();
  }

  public boolean canDecode(ColumnDefinitionPacket column, Class<?> type) {
    return column.getType() == DataType.BIT && type.isAssignableFrom(BitSet.class);
  }

  @Override
  public BitSet decodeText(
      ReadableByteBuf buf, int length, ColumnDefinitionPacket column, Calendar cal) {
    return parseBit(buf, length);
  }

  @Override
  public BitSet decodeBinary(
      ReadableByteBuf buf, int length, ColumnDefinitionPacket column, Calendar cal) {
    return parseBit(buf, length);
  }

  public boolean canEncode(Object value) {
    return value instanceof BitSet;
  }

  @Override
  public void encodeText(
      PacketWriter encoder, Context context, Object value, Calendar cal, Long length)
      throws IOException {
    byte[] bytes = ((BitSet) value).toByteArray();
    revertOrder(bytes);

    StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE + 3);
    sb.append("b'");
    for (int i = 0; i < Byte.SIZE * bytes.length; i++)
      sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
    sb.append("'");
    encoder.writeAscii(sb.toString());
  }

  @Override
  public void encodeBinary(PacketWriter encoder, Object value, Calendar cal, Long maxLength)
      throws IOException {
    byte[] bytes = ((BitSet) value).toByteArray();
    revertOrder(bytes);
    encoder.writeLength(bytes.length);
    encoder.writeBytes(bytes);
  }

  public int getBinaryEncodeType() {
    return DataType.BLOB.get();
  }
}
