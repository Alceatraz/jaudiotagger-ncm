@file:Suppress("unused")

package top.btswork.litetag.processor.flac.schema.block

import top.btswork.litetag.processor.flac.schema.block.Block.BlockType.PICTURE
import java.nio.ByteBuffer

@ConsistentCopyVisibility
data class PictureBlock private constructor(
  private val pictureType: Int,
  private val mimeType: String,
  private val description: String,
  private val width: Int,
  private val height: Int,
  private val depth: Int,
  private val colors: Int,
  private val picture: ByteBuffer,
) : Block {

  // ===================================================================================================================

  init {
    println("NEW PictureBlock: $pictureType mime=$mimeType desc=$description $width x $height $depth bit $colors ${picture.limit()}")
  }

  // ===================================================================================================================

  override fun getType() = PICTURE.value

  override fun serialize(): ByteBuffer {

    val mimeBytes = mimeType.toByteArray(Charsets.UTF_8)
    val descriptionBytes = description.toByteArray(Charsets.UTF_8)

    val mimeLength = mimeBytes.size
    val descriptionLength = descriptionBytes.size

    val pictureLength = picture.limit()

    val buffer = ByteBuffer.allocate(4 + 4 + mimeLength + 4 + descriptionLength + 4 + 4 + 4 + 4 + 4 + pictureLength)

    buffer.putInt(pictureType)
    buffer.putInt(mimeLength)
    buffer.put(mimeBytes)
    buffer.putInt(descriptionLength)
    buffer.put(descriptionBytes)
    buffer.putInt(width)
    buffer.putInt(height)
    buffer.putInt(depth)
    buffer.putInt(colors)
    buffer.putInt(pictureLength)
    buffer.put(picture)

    return buffer.flip().asReadOnlyBuffer()
  }

  // ===================================================================================================================

  companion object {

    fun getInstance(
      pictureType: Int,
      mime: String,
      description: String,
      width: Int,
      height: Int,
      depth: Int,
      colors: Int,
      picture: ByteBuffer,
    ) = PictureBlock(
      pictureType,
      mime,
      description,
      width,
      height,
      depth,
      colors,
      picture,
    )

    fun from(
      mime: String,
      picture: ByteBuffer,
      pictureType: Int = 3,
      description: String = "",
      width: Int = 0,
      height: Int = 0,
      depth: Int = 0,
      colors: Int = 0,
    ) = PictureBlock(
      pictureType,
      mime,
      description,
      width,
      height,
      depth,
      colors,
      picture,
    )

    fun from(buffer: ByteBuffer): PictureBlock {

//      require(buffer.remaining() >= 4) { "ERROR: missing picture type" }
//      require(buffer.remaining() >= 4) { "ERROR: missing mime length" }
//      require((mimeLength < 0 || buffer.remaining() < mimeLength).not()) { "ERROR: Invalid mime length -> $mimeLength" }

      val pictureType = buffer.int

      val mimeLength = buffer.int

      val mime = ByteArray(mimeLength).also {
        buffer.get(it)
      }.toString(Charsets.UTF_8)

      val descriptionLength = buffer.int

      val description = ByteArray(descriptionLength).also {
        buffer.get(it)
      }.toString(Charsets.UTF_8)

      val width = buffer.int
      val height = buffer.int
      val depth = buffer.int
      val colors = buffer.int
      val pictureLength = buffer.int

      val picture = buffer.slice().limit(pictureLength)

//      println("SLICE pictureLength=$pictureLength -> ${picture.position()} ${picture.limit()} ${picture.capacity()} ")
//
//      val paths = Paths.get("""C:\Temp\jaudio\image.jpg""")
//
//      Files.deleteIfExists(paths)
//      Files.createFile(paths)
//
//      val stream = Files.newByteChannel(paths, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
//
//      stream.write(picture)
//
//      TODO()

      return PictureBlock(pictureType, mime, description, width, height, depth, colors, picture)

    }

  }

  enum class PictureType(val value: Int) {

    OTHER(0),
    FILE_ICON(1),
    OTHER_FILE_ICON(2),
    FRONT_COVER(3),
    BACK_COVER(4),
    LEAFLET_PAGE(5),
    MEDIA(6),
    LEAD_ARTIST(7),
    ARTIST(8),
    CONDUCTOR(9),
    BAND(10),
    COMPOSER(11),
    LYRICIST(12),
    RECORDING_LOCATION(13),
    DURING_RECORDING(14),
    DURING_PERFORMANCE(15),
    VIDEO_SCREEN_CAPTURE(16),
    @Deprecated("这很诡异你知道吗") ABRIGHT_COLOURED_FISH(17),
    ILLUSTRATION(18),
    BAND_LOGO_TYPE(19),
    PUBLISHER_LOGO_TYPE(20);

    companion object {
      private val map = entries.associateBy(PictureType::value)
      fun from(type: Int) = map[type] ?: OTHER
    }

  }

}