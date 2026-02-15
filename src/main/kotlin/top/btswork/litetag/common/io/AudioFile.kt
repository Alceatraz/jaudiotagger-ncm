package top.btswork.litetag.common.io

import java.nio.ByteBuffer

interface AudioFile {

  fun read(buffer: ByteArray)
  fun read(buffer: ByteArray, offset: Int, length: Int)
  fun read(buffer: ByteArray, offset: Int, length: Int, index: Int)

  fun read(): Byte
  fun read(index: Int): Byte

  fun readShort(): Short
  fun readShort(index: Int): Short

  fun readInt(): Int
  fun readInt(index: Int): Int

  fun readLong(): Long
  fun readLong(index: Int): Long

  fun readChar(): Char
  fun readChar(index: Int): Char

  fun copy(srcPosition: Int = 0, destPosition: Int = 0, length: Int = 0)

  fun write(byte: Byte)
  fun write(bytes: ByteArray)
  fun write(bytes: ByteArray, offset: Int, length: Int)
  fun write(bytes: ByteArray, offset: Int, length: Int, index: Int)

  fun write(buffer: ByteBuffer)
  fun write(buffer: ByteBuffer, offset: Int, length: Int, index: Int)

  fun seek(position: Int)
  fun skip(position: Int)

  fun slice(position: Int, length: Int): ByteBuffer

  fun clear()
  fun truncate(size: Int)

  fun getReadBuffer(): ByteBuffer
  fun getWriteBuffer(): ByteBuffer

  fun readPosition(): Int
  fun readLimit(): Int
  fun readRemaining(): Int
  fun readCapacity(): Int

  fun writePosition(): Int
  fun writeLimit(): Int
  fun writeRemaining(): Int
  fun writeCapacity(): Int

}
