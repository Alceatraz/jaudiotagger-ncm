package top.btswork.litetag

import top.btswork.litetag.common.io.AudioFile
import top.btswork.litetag.processor.flac.FlacAudioFile
import top.btswork.litetag.processor.mp3.Mp3AudioFile

object LiteTag {

  fun getMp3Instance(audioFile: AudioFile) = Mp3AudioFile(audioFile)

  fun getFlacInstance(audioFile: AudioFile) = FlacAudioFile(audioFile)

}