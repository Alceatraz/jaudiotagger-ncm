package top.btswork.litetag.common.impl

import top.btswork.litetag.common.io.impl.MemoryAudioFile
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.test.Test

class MemoryAudioFileTest {

  @Test
  fun test01() {

    val input = Paths.get("""C:\Temp\jaudio\test1.flac""")
    val output = Paths.get("""C:\Temp\jaudio\test1-audiopfile.flac""")

    val audioFile = MemoryAudioFile(ByteBuffer.wrap(Files.readAllBytes(input)))

    val also = ByteArray(4).also { audioFile.read(it) }

    println(also.contentToString())
    println(String(also, StandardCharsets.UTF_8))

    audioFile.save {
      Files.write(output, it.array(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }

  }

}