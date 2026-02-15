@file:Suppress("unused")

package top.btswork.litetag.processor.flac.schema.block

import top.btswork.litetag.processor.flac.schema.block.Block.BlockType.PADDING
import java.nio.ByteBuffer

@ConsistentCopyVisibility
data class PaddingBlock private constructor(
  private val size: Int,
) : Block {

  // ===================================================================================================================

  init {
    println("NEW PaddingBlock: size=$size")
  }

  // ===================================================================================================================

  override fun getType() = PADDING.value

  override fun serialize(): ByteBuffer = ByteBuffer.wrap(ByteArray(size)).asReadOnlyBuffer()

  // ===================================================================================================================

  companion object {

    fun getInstance(size: Int) = PaddingBlock(size)

  }

}