package top.btswork.litetag.processor.mp3.schema

import top.btswork.litetag.common.io.AudioFile
import java.nio.ByteBuffer

class Mp3Metadata {

  private lateinit var schema: ID3v2

  private val textTag: MutableMap<String, String> = LinkedHashMap()
  private val streamTag: MutableMap<String, ByteBuffer> = LinkedHashMap()

  //= ====================================================================================================================

  //= ====================================================================================================================

  companion object {

    fun parse(audioFile: AudioFile) = Mp3Metadata().apply {

      val version = audioFile.read()
      val revision = audioFile.read()

      println("ID3 version = $version.$revision")

      schema = when (version.toInt()) {
        2 -> ID3v22(audioFile)
        3 -> ID3v23(audioFile)
        4 -> ID3v24(audioFile)
        else -> throw IllegalArgumentException("Unknown MP3 version $version")
      }

      schema.parse()

    }

  }

}

//= ====================================================================================================================

abstract class Mp3Common {

  /**
   * Parse a 32-bit synchsafe integer stored in the low 28 bits of [value].
   * Each byte uses only 7 bits: 0xxxxxxx 0xxxxxxx 0xxxxxxx 0xxxxxxx
   */
  protected fun parseSynchsafe(value: Int): Int {
    val b0 = (value ushr 24) and 0x7F
    val b1 = (value ushr 16) and 0x7F
    val b2 = (value ushr 8) and 0x7F
    val b3 = value and 0x7F
    return (b0 shl 21) or (b1 shl 14) or (b2 shl 7) or b3
  }

  /**
   * Parse a 32-bit synchsafe integer from a 4-byte slice.
   * The buffer must have position=0, limit=4 (e.g. from slice()).
   */
  protected fun parseSynchsafe(buffer: ByteBuffer): Int {
    require(buffer.remaining() == 4) {
      "Synchsafe int must be 4 bytes"
    }
    val b0 = buffer.get(0).toInt() and 0x7F
    val b1 = buffer.get(1).toInt() and 0x7F
    val b2 = buffer.get(2).toInt() and 0x7F
    val b3 = buffer.get(3).toInt() and 0x7F
    return (b0 shl 21) or (b1 shl 14) or (b2 shl 7) or b3
  }

}

abstract class ID3v2(protected val audioFile: AudioFile) : Mp3Common() {
  abstract fun parse()
}

//= ====================================================================================================================

private class ID3v22(audioFile: AudioFile) : ID3v2(audioFile) {
  override fun parse() {
    TODO("Not yet implemented")
  }
}

//= ====================================================================================================================

private class ID3v23(audioFile: AudioFile) : ID3v2(audioFile) {

  override fun parse() {

    val flag = audioFile.read().toInt() and 0xFF

    val unsync = flag and 0b10000000 != 0
    val extended = flag and 0b01000000 != 0
    val experimental = flag and 0b00100000 != 0

    println("flag: $flag = unsync: $unsync extended: $extended experimental: $experimental")

    val size = parseSynchsafe(audioFile.readInt())

    println(size)
    println(size + audioFile.readPosition())

    if (extended) {
      throw IllegalArgumentException("CONTAINS EXTEND HEADER")
    }

  }

}

//= ====================================================================================================================

private class ID3v24(audioFile: AudioFile) : ID3v2(audioFile) {

  override fun parse() {

    val flag = audioFile.read().toInt() and 0xFF

    val unsync = flag and 0b10000000 != 0
    val extended = flag and 0b01000000 != 0
    val footerPresent = flag and 0b00100000 != 0

    println("flag: $flag = unsync: $unsync extended: $extended footerPresent: $footerPresent")

    val size = parseSynchsafe(audioFile.readInt())

    println(size)
    println(size + audioFile.readPosition())

    if (extended) {
      throw IllegalArgumentException("CONTAINS EXTEND HEADER")
    }

    println("This is v24")

  }

}

//= ====================================================================================================================
