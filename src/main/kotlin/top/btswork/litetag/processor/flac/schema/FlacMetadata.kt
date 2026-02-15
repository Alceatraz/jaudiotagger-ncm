package top.btswork.litetag.processor.flac.schema

import top.btswork.litetag.common.io.AudioFile
import top.btswork.litetag.processor.flac.schema.block.Block
import top.btswork.litetag.processor.flac.schema.block.Block.BlockType
import top.btswork.litetag.processor.flac.schema.block.Block.BlockType.CUESHEET
import top.btswork.litetag.processor.flac.schema.block.Block.BlockType.PADDING
import top.btswork.litetag.processor.flac.schema.block.Block.BlockType.PICTURE
import top.btswork.litetag.processor.flac.schema.block.Block.BlockType.SEEKTABLE
import top.btswork.litetag.processor.flac.schema.block.Block.BlockType.STREAMINFO
import top.btswork.litetag.processor.flac.schema.block.Block.BlockType.VORBIS_COMMENT
import top.btswork.litetag.processor.flac.schema.block.OtherBlock
import top.btswork.litetag.processor.flac.schema.block.PaddingBlock
import top.btswork.litetag.processor.flac.schema.block.PictureBlock
import top.btswork.litetag.processor.flac.schema.block.VorbisBlock
import java.nio.ByteBuffer

class FlacMetadata {

  private val blocks = ArrayList<Block>()

  //= ==================================================================================================================

  fun getBlocks() = blocks

  fun hasVorbis() = blocks.any {
    it.getType() == VORBIS_COMMENT.value
  }

  fun getOrNewVorbis(): VorbisBlock {
    val result = blocks.filter {
      it.getType() == VORBIS_COMMENT.value
    }
    return if (result.isEmpty()) {
      VorbisBlock("LiteTAG").also {
        blocks.add(it)
      }
    } else {
      result[0] as VorbisBlock
    }
  }

  fun serialize(write: (ByteBuffer) -> Unit) {

    val streamInfo = blocks[0]

    var seekTable: Block? = null
    var vorbisComment: Block? = null
    var cueSheet: Block? = null
    val pictures = ArrayList<Block>()
    val other = ArrayList<Block>()

    blocks.forEachIndexed { index, block ->
      if (index == 0) return@forEachIndexed
      when (block.getType()) {
        SEEKTABLE.value -> seekTable = block
        VORBIS_COMMENT.value -> vorbisComment = block
        CUESHEET.value -> cueSheet = block
        PICTURE.value -> pictures.add(block)
        PADDING.value -> {}
        else -> other.add(block)
      }
    }

    run {
      val serialize: ByteBuffer = streamInfo.serialize()
      val blockHead = calculate(STREAMINFO.value, serialize)
      write(blockHead)
      write(serialize)
    }

    if (seekTable != null) {
      val serialize = seekTable.serialize()
      val blockHead = calculate(SEEKTABLE.value, serialize)
      write(blockHead)
      write(serialize)
    }

    if (vorbisComment != null) {
      val serialize = vorbisComment.serialize()
      val blockHead = calculate(VORBIS_COMMENT.value, serialize)
      write(blockHead)
      write(serialize)
    }

    if (cueSheet != null) {
      val serialize = cueSheet.serialize()
      val blockHead = calculate(CUESHEET.value, serialize)
      write(blockHead)
      write(serialize)
    }

    pictures.forEach {
      val serialize = it.serialize()
      val blockHead = calculate(PICTURE.value, serialize)
      write(blockHead)
      write(serialize)
    }

    other.forEach {
      val serialize = it.serialize()
      val blockHead = calculate(it.getType(), serialize)
      write(blockHead)
      write(serialize)
    }

    // PADDING

    run {
      val padding = PaddingBlock.getInstance(1024)
      val serialize = padding.serialize()
      val blockHead = calculate(PADDING.value, serialize, true)
      write(blockHead)
      write(serialize)
    }

  }

  // ===================================================================================================================

  companion object {

    fun parse(audioFile: AudioFile) = FlacMetadata().apply {

      while (true) {

        val head = audioFile.readInt()

        val lastFlag = head < 0
        val blockType = head shr 24 and 0b01111111
        val blockLength: Int = head and 0b00000000_11111111_11111111_11111111

        val position = audioFile.readPosition()

        println(" >> $position")

        val slice = audioFile.getReadBuffer().slice(position, blockLength)

        val typeId = blockType.toByte()

        println(slice)

        val block = when (blockType) {
          4 -> VorbisBlock.Companion.from(slice)
          6 -> PictureBlock.Companion.from(slice)
          else -> OtherBlock.Companion.getInstance(typeId, slice)
        }

        audioFile.skip(blockLength)

        println(" << " + audioFile.readPosition())

        blocks.add(block)

        println("PARSE -> LAST: $lastFlag, TYPE: ${BlockType.from(typeId)}, LENGTH: $blockLength")

        if (lastFlag) break

      }

    }

    private fun calculate(
      blockType: Byte,
      blockPayload: ByteBuffer,
      lastBlockFlag: Boolean = false,
    ) = ByteBuffer.allocate(4).also {
      var i = 0
      i = i or ((if (lastBlockFlag) 1 else 0) shl 31)
      i = i or ((blockType.toInt() and 0x7F) shl 24)
      i = i or (blockPayload.limit() and 0x00FFFFFF)
      it.putInt(i)
      // println(" HEAD " + i.toHexString())
    }.flip()

  }

}