@file:Suppress("unused")

package top.btswork.litetag.processor.flac.schema.block

import top.btswork.litetag.processor.flac.schema.block.Block.BlockType.VORBIS_COMMENT
import java.nio.ByteBuffer

data class VorbisBlock(val vendor: String, val vorbisComments: VorbisComments = VorbisComments()) : Block {

  override fun getType() = VORBIS_COMMENT.value

  override fun serialize(): ByteBuffer {

    val vendorBytes = vendor.toByteArray(Charsets.UTF_8)
    val commentBytesList = vorbisComments.store.map { "${it.first}=${it.second}".toByteArray(Charsets.UTF_8) }

    var totalSize = 4 + vendorBytes.size + 4
    commentBytesList.forEach { totalSize += 4 + it.size }

    val buffer = ByteBuffer.allocate(totalSize)

    buffer.putInt(Integer.reverseBytes(vendorBytes.size))
    buffer.put(vendorBytes)

    buffer.putInt(Integer.reverseBytes(commentBytesList.size))

    commentBytesList.forEach {
      buffer.putInt(Integer.reverseBytes(it.size))
      buffer.put(it)
    }

    return buffer.flip().asReadOnlyBuffer()
  }

  companion object {

    fun getInstance(vendor: String) = VorbisBlock(vendor, VorbisComments())

    fun from(buffer: ByteBuffer): VorbisBlock {
      val length = Integer.reverseBytes(buffer.getInt())
      println("VORBIS vendor length $length")
      val vendor = ByteArray(length).also { buffer.get(it) }
      println("VORBIS vendor data " + String(vendor))
      val vorbisComments = VorbisComments.parse(buffer)
      return VorbisBlock(String(vendor), vorbisComments)
    }

  }

  /**
   *
   * TITLE	曲名
   * VERSION	版本/混音信息
   * ALBUM	专辑名
   * TRACKNUMBER	曲目号
   * ARTIST	艺术家（流行音乐）/作曲家（古典）
   * PERFORMER	表演者（古典：指挥/乐团/独奏）
   * COPYRIGHT	版权信息
   * LICENSE	授权信息
   * ORGANIZATION	出版方（唱片公司）
   * DESCRIPTION	简短描述
   * GENRE	流派
   * DATE	录制日期
   * LOCATION	录制地点
   * CONTACT	联系方式
   * ISRC	国际标准录音代码
   *
   *
   * ALBUMARTIST	专辑艺术家（非常常用）
   * ALBUMARTISTSORT	排序用（如 Beatles, The → Beatles）
   * ALBUMSORT	专辑排序名
   *
   * TITLESORT	排序用标题
   * TRACKTOTAL / TOTALTRACKS	专辑总曲目数
   * DISCNUMBER	碟号
   * DISCTOTAL / TOTALDISCS	总碟数
   *
   *
   * ARTISTS	多艺术家列表（MusicBrainz）
   * ARTISTSORT	排序用艺术家名
   * COMPOSER	作曲家
   * COMPOSERSORT	排序用作曲家名
   * LYRICIST	作词者
   * CONDUCTOR	指挥
   * ENSEMBLE	乐团
   * ARRANGER	编曲
   *
   *
   * COMMENT	注释（最常见）
   * LYRICS	歌词（不推荐，见下）
   * UNSYNCEDLYRICS	无时间轴歌词（更常见）
   * SYNCLYRICS	有时间轴歌词
   *
   *
   * REPLAYGAIN_TRACK_GAIN	单曲增益
   * REPLAYGAIN_TRACK_PEAK	单曲峰值
   * REPLAYGAIN_ALBUM_GAIN	专辑增益
   * REPLAYGAIN_ALBUM_PEAK	专辑峰值
   *
   *
   * ENCODER	编码器名称
   * ENCODING	编码设置
   * ENCODERSETTINGS	编码参数
   *
   */
  data class VorbisComments(val store: MutableList<Pair<String, String>> = ArrayList()) {

    companion object {
      fun parse(buffer: ByteBuffer): VorbisComments {
        val commentCount = Integer.reverseBytes(buffer.getInt())
        println("VORBIS comment count $commentCount")
        val instance = VorbisComments()
        for (i in 0 until commentCount) {
          val itemLength = Integer.reverseBytes(buffer.getInt())
          val item = ByteArray(itemLength).also { buffer.get(it) }
          val itemString = String(item)
          val index = itemString.indexOf("=")
          val key = itemString.substring(0, index).trim()
          val value = itemString.substring(index + 1).trim()
          println("$i $key = $value")
          instance.add(key, value)
        }
        return instance
      }
    }

    fun add(key: String, value: String) {
      store.add(key.uppercase() to value)
    }

    fun removeAll(key: String) {
      val iterator = store.iterator()
      val name = key.uppercase()
      while (iterator.hasNext()) {
        val comment = iterator.next()
        if (comment.first == name) {
          iterator.remove()
        }
      }
    }
  }

}