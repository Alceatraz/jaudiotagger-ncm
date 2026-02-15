package top.btswork.litetag.processor.flac

import top.btswork.litetag.common.io.impl.MemoryAudioFile
import top.btswork.litetag.LiteTag
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test

class Mp3LiteTagTest {

  val output = Paths.get("""C:\Temp\jaudio\PROCESSOR-TEST.mp3""")

  val input1 = Paths.get("""C:\Temp\jaudio\test1.mp3""")
  val input2 = Paths.get("""C:\Temp\jaudio\test2.mp3""")
  val input3 = Paths.get("""C:\Temp\jaudio\test3.mp3""")
  val input4 = Paths.get("""C:\Temp\jaudio\test4.mp3""")
  val input5 = Paths.get("""C:\Temp\jaudio\test5.mp3""")
  val input6 = Paths.get("""C:\Temp\jaudio\test6.mp3""")

  @Test
  fun test01() {

    arrayOf(
      input1,
      input2,
      input3,
      input4,
      input5,
      input6,
    ).forEach {

      System.err.println(">>>> $it")

      Files.deleteIfExists(output)
      Files.createFile(output)

      val audioFile = MemoryAudioFile(Files.readAllBytes(it))

      val mp3AudioFile = LiteTag.getMp3Instance(audioFile)

      mp3AudioFile.checkMagicHead()

      mp3AudioFile.load()

      Thread.sleep(1)

    }

  }
}