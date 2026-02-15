@file:Suppress("SpellCheckingInspection")

package top.btswork.litetag.processor.flac.schema.block

import java.nio.ByteBuffer

interface Block {

  fun getType(): Byte
  fun serialize(): ByteBuffer

  enum class BlockType(val value: Byte) {

    STREAMINFO(0),
    PADDING(1),
    APPLICATION(2),
    SEEKTABLE(3),
    VORBIS_COMMENT(4),
    CUESHEET(5),
    PICTURE(6),

    OTHER(-1)
    ;

    companion object {
      private val map = entries.associateBy(BlockType::value)
      fun from(type: Byte) = map[type] ?: OTHER
    }

  }

}

