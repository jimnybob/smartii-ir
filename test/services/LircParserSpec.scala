package services

import org.scalatest.{FlatSpec, MustMatchers}
import org.scalamock.scalatest.MockFactory
import scala.sys.process._

/**
  * Created by jimbo on 02/01/17.
  */
class LircParserSpec extends FlatSpec with MustMatchers with MockFactory {

  "The irsend command" should "find devices" in {

    val process = mock[ProcessCreation]
    val builder = mock[ProcessBuilder]

    (process.apply(_:String,_:Seq[String])).expects("irsend", Seq("list", "", "")).returns(builder)
    (builder.lineStream_! _).expects().returns(Stream("irsend: sony", "irsend: jvc"))

    val lircParser = new LircParser(process)
    lircParser.listDevices must be(Seq("sony", "jvc"))
  }

  it should "find buttons for a device" in {

    val process = mock[ProcessCreation]
    val builder = mock[ProcessBuilder]
    (process.apply(_:String,_:Seq[String])).expects("irsend", Seq("list", "sony", "")).returns(builder)
    (builder.lineStream_! _).expects().returns(Stream("irsend: 0000000000000481 KEY_VOLUMEUP",
      "irsend: 0000000000000c81 KEY_VOLUMEDOWN"))

    val lircParser = new LircParser(process)
    lircParser.listButtons("sony") must be(Seq("KEY_VOLUMEUP", "KEY_VOLUMEDOWN"))
  }

  it should "press buttons for a device" in {

    val process = mock[ProcessCreation]
    val builder = mock[ProcessBuilder]
    (process.apply(_:String,_:Seq[String])).expects("irsend", Seq("SEND_ONCE", "sony", "KEY_VOLUMEUP")).returns(builder)
    (builder.! _).expects().returns(0)

    val lircParser = new LircParser(process)
    lircParser.pressButton("sony", "KEY_VOLUMEUP") must be(true)
  }
}
