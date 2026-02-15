package top.btswork.litetag.processor.flac

import top.btswork.litetag.common.io.AudioFile
import top.btswork.litetag.processor.flac.schema.FlacMetadata

class FlacAudioFile(private val audioFile: AudioFile) {

  companion object {
    private val FLAC_MAGIC = byteArrayOf(0x66, 0x4C, 0x61, 0x43) // "fLaC"
  }

  private lateinit var metadata: FlacMetadata

  fun checkMagicHead(): Boolean {
    val fLaC = ByteArray(4).also {
      audioFile.read(it)
    }
    return FLAC_MAGIC.contentEquals(fLaC)
  }

  fun load() {
    metadata = FlacMetadata.parse(audioFile)
  }

  fun save() {
    audioFile.write(FLAC_MAGIC)
    metadata.serialize {
      println("BUFFER ${it.position()} / ${it.limit()} ${it.remaining()} / ${it.capacity()} > ${audioFile.writePosition()} / ${audioFile.writeLimit()}")
      audioFile.write(it)
    }
    val srcPosition = audioFile.readPosition()
    val destPosition = audioFile.writePosition()
    audioFile.copy(srcPosition, destPosition)
  }

  fun invoke(func: (AudioFile) -> Unit) {
    func(audioFile)
  }

  fun getMetadata() = metadata

}