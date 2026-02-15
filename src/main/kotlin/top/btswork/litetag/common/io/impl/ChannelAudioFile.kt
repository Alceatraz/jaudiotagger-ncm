@file:Suppress("unused")

package top.btswork.litetag.common.io.impl

import top.btswork.litetag.common.io.AudioFile
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode.READ_ONLY
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE

class ChannelAudioFile : AudioFile {

  constructor(inputChannel: FileChannel, outputChannel: FileChannel) {
    this.inputChannel = inputChannel
    this.outputChannel = outputChannel
    this.readBuffer = inputChannel.map(READ_ONLY, 0, inputChannel.size())
    this.writeBuffer = ByteBuffer.allocate(8192)
  }

  constructor(sourcePath: Path, targetPath: Path) : this(
    FileChannel.open(sourcePath, READ),
    FileChannel.open(targetPath, WRITE, CREATE, TRUNCATE_EXISTING),
  )

  // ===========================================================================

  private val inputChannel: FileChannel
  private val outputChannel: FileChannel

  private val readBuffer: MappedByteBuffer
  private var writeBuffer: ByteBuffer

  // ===========================================================================

  fun save(func: (ByteBuffer) -> Unit) {
    writeBuffer.flip()
    func(writeBuffer)
    writeBuffer.clear()
  }

  fun save() {
    writeBuffer.flip()
    while (writeBuffer.hasRemaining()) {
      outputChannel.write(writeBuffer)
    }
    writeBuffer.clear()
  }

  fun closeSourceChannel() {
    inputChannel.close()
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
    outputChannel.position(destPosition.toLong())
    val remain = (if (length == 0) inputChannel.size() else length).toLong()
    var remaining = remain
    while (remaining > 0) {
      val transferred = inputChannel.transferTo(
        srcPosition.toLong(),
        remaining,
        outputChannel
      )
      if (transferred <= 0) {
        throw IOException("Failed to transfer data from source to destination")
      }
      remaining -= transferred
    }
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
        (writeBuffer.capacity() * 1.5).toInt(),
        minCapacity,
        1024
      )
      val newBuffer = ByteBuffer.allocate(newCapacity)
      writeBuffer.flip()
      newBuffer.put(writeBuffer)
      writeBuffer = newBuffer
    }
  }

}