@file:Suppress("unused")

package top.btswork.litetag.common.io.impl

import top.btswork.litetag.common.io.AudioFile
import java.nio.ByteBuffer

class MemoryAudioFile : AudioFile {

  constructor(data: ByteArray) {
    this.readBuffer = ByteBuffer.wrap(data)
    this.writeBuffer = ByteBuffer.allocate(1024768).rewind()
  }

  constructor(data: ByteBuffer) {
    this.readBuffer = data.asReadOnlyBuffer().rewind()
    this.writeBuffer = ByteBuffer.allocate(1024768).rewind()
  }

  // ===========================================================================

  private val readBuffer: ByteBuffer
  private var writeBuffer: ByteBuffer

  // ===========================================================================

  fun save(func: (ByteBuffer) -> Unit) {
    writeBuffer.flip()
    println("write buffer ${writeBuffer.position()}")
    println("write buffer ${writeBuffer.limit()}")
    println("write buffer ${writeBuffer.capacity()}")
    func(writeBuffer)
  }

  // ===========================================================================

  override fun read(buffer: ByteArray) {
    readBuffer.get(buffer)
  }

  override fun read(buffer: ByteArray, offset: Int, length: Int) {
    readBuffer.get(buffer, offset, length)
  }

  override fun read(buffer: ByteArray, offset: Int, length: Int, index: Int) {
    readBuffer.get(index, buffer, offset, length)
  }

  // ===========================================================================

  override fun read(): Byte {
    return readBuffer.get()
  }

  override fun read(index: Int): Byte {
    return readBuffer.get(index)
  }

  override fun readShort(): Short {
    return readBuffer.getShort()
  }

  override fun readShort(index: Int): Short {
    return readBuffer.getShort(index)
  }

  override fun readInt(): Int {
    return readBuffer.getInt()
  }

  override fun readInt(index: Int): Int {
    return readBuffer.getInt(index)
  }

  override fun readLong(): Long {
    return readBuffer.getLong()
  }

  override fun readLong(index: Int): Long {
    return readBuffer.getLong(index)
  }

  override fun readChar(): Char {
    return readBuffer.getChar()
  }

  override fun readChar(index: Int): Char {
    return readBuffer.getChar(index)
  }
  // ===========================================================================

  override fun copy(srcPosition: Int, destPosition: Int, length: Int) {
    require(srcPosition >= 0 && destPosition >= 0) {
      "srcPosition, destPosition be non-negative"
    }

    val remaining = if (length == 0) (readBuffer.capacity() - readBuffer.position()) else length

    println("COPY -> SRC: $srcPosition, DEST: $destPosition, LEN: $length, REMAINING: $remaining")

    val buffer = ByteArray(remaining)
    readBuffer.get(buffer, 0, remaining)

    val minCapacity = destPosition + remaining

    val currentCapacity = writeBuffer.capacity()

    ensureCapacity(minCapacity)

    println("ENSURE $minCapacity $currentCapacity -> ${writeBuffer.capacity()}")

    writeBuffer.put(destPosition, buffer, 0, remaining)

    println("WRITE -> position: ${writeBuffer.position()} - limit: ${writeBuffer.limit()} - capacity: ${writeBuffer.capacity()}")

    writeBuffer.limit(writeBuffer.position())

    println("WRITE -> position: ${writeBuffer.position()} - limit: ${writeBuffer.limit()} - capacity: ${writeBuffer.capacity()}")

  }

  // ===========================================================================

  override fun write(byte: Byte) {
    ensureCapacity(writeBuffer.position() + 1)
    writeBuffer.put(byte)
  }

  override fun write(bytes: ByteArray) {
    ensureCapacity(writeBuffer.position() + bytes.size)
    writeBuffer.put(bytes)
  }

  override fun write(bytes: ByteArray, offset: Int, length: Int) {
    ensureCapacity(writeBuffer.position() + length)
    writeBuffer.put(bytes, offset, length)
  }

  override fun write(bytes: ByteArray, offset: Int, length: Int, index: Int) {
    ensureCapacity(index + length)
    writeBuffer.put(index, bytes, offset, length)
  }

  override fun write(buffer: ByteBuffer) {
    ensureCapacity(writeBuffer.position() + buffer.limit())
    writeBuffer.put(buffer)
  }

  override fun write(buffer: ByteBuffer, offset: Int, length: Int, index: Int) {
    ensureCapacity(writeBuffer.position() + buffer.limit())
    writeBuffer.put(index, buffer, offset, length)
  }

  // ===========================================================================

  override fun seek(position: Int) {
    readBuffer.position(position)
  }

  override fun skip(position: Int) {
    readBuffer.position(readBuffer.position() + position)
  }

  override fun slice(position: Int, length: Int) = readBuffer.slice(position, length)!!

  override fun clear() {
    writeBuffer.clear()
  }

  override fun truncate(size: Int) {
    require(size >= 0) {
      "Size cannot be negative"
    }
    if (writeBuffer.capacity() < size) {
      ensureCapacity(size)
    } else {
      writeBuffer.position(size)
    }
  }

  // ===========================================================================

  override fun getReadBuffer() = readBuffer
  override fun getWriteBuffer() = writeBuffer

  override fun readPosition() = readBuffer.position()
  override fun readLimit() = readBuffer.limit()
  override fun readRemaining() = readBuffer.remaining()
  override fun readCapacity() = readBuffer.capacity()

  override fun writePosition() = writeBuffer.position()
  override fun writeLimit() = writeBuffer.limit()
  override fun writeRemaining() = writeBuffer.remaining()
  override fun writeCapacity() = writeBuffer.capacity()

  // ===========================================================================

  private fun ensureCapacity(minCapacity: Int) {
    if (minCapacity > writeBuffer.capacity()) {
      val newCapacity = maxOf(
        (writeBuffer.capacity() * 2).toInt(),
        minCapacity,
        1024
      )
      val newBuffer = ByteBuffer.allocate(newCapacity)
      writeBuffer.flip()
      newBuffer.put(writeBuffer)
      writeBuffer = newBuffer
    }
  }

  // ===========================================================================

}

