package top.btswork.litetag.processor.flac

import top.btswork.litetag.common.io.impl.MemoryAudioFile
import top.btswork.litetag.LiteTag
import top.btswork.litetag.processor.flac.schema.block.PictureBlock
import top.btswork.litetag.processor.flac.schema.block.type.BlockType
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test

class FlacLiteTagTest {

  val input = Paths.get("""C:\Temp\jaudio\test1.flac""")
  val output = Paths.get("""C:\Temp\jaudio\PROCESSOR-TEST.flac""")

  @Test
  fun test01() {

    Files.deleteIfExists(output)
    Files.createFile(output)

    val audioFile = MemoryAudioFile(Files.readAllBytes(input))

    val flacAudioFile = LiteTag.getFlacInstance(audioFile)

    flacAudioFile.checkMagicHead()

    flacAudioFile.load()

    val metadata = flacAudioFile.getMetadata()

    val iterator = metadata.getBlocks().iterator()

    iterator.forEach {
      if (it.getType() == BlockType.PICTURE.value) iterator.remove()
    }

    val image = Paths.get("""C:\Temp\jaudio\test-cover.jpg""")
    val imageBytes = Files.readAllBytes(image)

    val wrap = ByteBuffer.wrap(imageBytes)

    PictureBlock.from("image/jpeg", wrap).also {
      metadata.getBlocks().add(it)
    }

//    if (metadata.hasVorbisBlock()) {
//      metadata.getVorbisBlock()!!.vorbisComments.removeAll("DESCRIPTION")
//    }

//    metadata.removeVorbisBlock()

//    val oldVorbis = metadata.removeVorbisBlock()!!.vorbisComments
//
//    val vorbisBlock = metadata.createVorbisBlock()
//

    val vorbisBlock = metadata.getOrNewVorbis()

    vorbisBlock.vorbisComments.removeAll("DESCRIPTION")

//
//    vorbisComment.add("TITLE", "This is new shit")
//    vorbisComment.add("TRACKNUMBER", "7")
//    vorbisComment.add("TITLESORT", "7")
//    vorbisComment.add("DISCNUMBER", "1")
//    vorbisComment.add("DISCTOTAL", "1")

    System.err.println("================================================================================================")

    flacAudioFile.save()

    audioFile.save {
      Files.write(
        output,
        it.array()
      )
    }

  }

  @Test
  fun test02() {

    val audioFile = MemoryAudioFile(Files.readAllBytes(output))

    val flacAudioFile = LiteTag.getFlacInstance(audioFile)

    flacAudioFile.checkMagicHead()

    flacAudioFile.load()

  }

  @Test
  fun test03() {

    val raw = Files.readAllBytes(input)
    val res = Files.readAllBytes(output)

    raw.forEachIndexed { index, byte ->

      if (res[index] == byte) {

      } else {
        throw IllegalArgumentException(index.toString())
      }
    }

  }

  @Test
  fun test00() {
    // 03 00 08 4C
    // 00 03 0b 4c

    val lastBlockFlag = false
    val blockType = 3.toByte()

    var i = 0
    i = i or ((if (lastBlockFlag) 1 else 0) shl 31)
    i = i or ((blockType.toInt() and 0x7F) shl 24)
    i = i or (0x84c and 0x00FFFFFF)
    println(i.toHexString())
  }

}