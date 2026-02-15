package top.btswork.litetag.processor.flac.schema.block

import java.nio.ByteBuffer

@ConsistentCopyVisibility
data class OtherBlock private constructor(
  private val blockType: Byte,
  private val blockPayload: ByteBuffer,
) : Block {

  // ===================================================================================================================

  init {
    println("NEW BasicBlock: $blockType LIMIT ${blockPayload.limit()}")
  }

  // ===================================================================================================================

  override fun getType() = blockType

  override fun serialize(): ByteBuffer = blockPayload.asReadOnlyBuffer()

  // ===================================================================================================================

  companion object {

    fun getInstance(blockType: Byte, blockPayload: ByteBuffer) = OtherBlock(blockType, blockPayload)

  }

}