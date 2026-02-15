package top.btswork.litetag.processor.mp3

import top.btswork.litetag.common.io.AudioFile
import top.btswork.litetag.processor.mp3.schema.Mp3Metadata

class Mp3AudioFile(private val audioFile: AudioFile) {

  companion object {

    private val ID3V2_MAGIC = byteArrayOf(0x49, 0x44, 0x33)

    private val ID3V22_FLAG = byteArrayOf(0x02, 0x00, 0x00)
    private val ID3V23_FLAG = byteArrayOf(0x03, 0x00, 0x00)
    private val ID3V24_FLAG = byteArrayOf(0x04, 0x00, 0x00)

  }

  private lateinit var metadata: Mp3Metadata

  fun checkMagicHead(): Boolean {
    val magic = ByteArray(3).also {
      audioFile.read(it)
    }
    return ID3V2_MAGIC.contentEquals(magic)
  }

  fun load() {
    metadata = Mp3Metadata.parse(audioFile)
  }

}
