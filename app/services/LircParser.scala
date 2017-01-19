package services

import javax.inject.{Inject, Provider, Singleton}

import com.google.inject.ImplementedBy
import io.TestableProcessLogger

import scala.collection.mutable.ListBuffer
import scala.sys.process._

trait LircParser {

  def listDevices: Seq[String]
  def listButtons(device: String): Seq[String]
  def pressButton(device: String, button: String): Boolean
}

/**
  * Created by jimbo on 02/01/17.
  */
@Singleton
class DefaultLircParser @Inject()(process: ProcessCreation, processLoggerProvider: Provider[TestableProcessLogger]) extends LircParser {

  override def listDevices: Seq[String] = {

    val processLogger = processLoggerProvider.get()
    // Get 2nd column of irsend list
    process("irsend", Seq("list", "", "")).lineStream_!(processLogger.processLogger)
    processLogger.lines.map(_.split(" ").apply(1))
  }

  override def listButtons(device: String): Seq[String] = {

    val processLogger = processLoggerProvider.get()
    // Get 3rd column of irsend list
    process("irsend", Seq("list", device, "")).lineStream_!(processLogger.processLogger)
    processLogger.lines.map(_.split(" ").apply(2))
  }

  /**
    *
    * @param device
    * @param button
    * @return true if success exit code
    */
  override def pressButton(device: String, button: String): Boolean = {
    process("irsend", Seq("SEND_ONCE", device, button)).! == 0
  }
}
