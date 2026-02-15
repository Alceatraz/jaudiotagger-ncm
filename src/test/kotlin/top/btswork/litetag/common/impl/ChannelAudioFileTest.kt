package top.btswork.litetag.common.impl

import top.btswork.litetag.common.io.impl.ChannelAudioFile
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import kotlin.test.Test

class ChannelAudioFileTest {

  @Test
  fun test01() {

    val input = Paths.get("""C:\Temp\jaudio\test1.flac""")
    val output = Paths.get("""C:\Temp\jaudio\test1-audiopfile.flac""")

    val audioFile = ChannelAudioFile(input, output)

    val also = ByteArray(4).also { audioFile.read(it) }

    println(also.contentToString())
    println(String(also, StandardCharsets.UTF_8))

    audioFile.copy()

  }

}