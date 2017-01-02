package services

import javax.inject.{Inject, Singleton}

import scala.sys.process._

/**
  * Created by jimbo on 02/01/17.
  */
@Singleton
class LircParser @Inject()(process: ProcessCreation) {

  def listDevices: Seq[String] = {

    // Get 2nd column of irsend list
    process("irsend", Seq("list", "", "")).lineStream_!.map(_.split(" ").apply(1))
  }

  def listButtons(device: String): Seq[String] = {
    // Get 3rd column of irsend list
    process("irsend", Seq("list", device, "")).lineStream_!.map(_.split(" ").apply(2))
  }

  /**
    *
    * @param device
    * @param button
    * @return true if success exit code
    */
  def pressButton(device: String, button: String): Boolean = {
    process("irsend", Seq("SEND_ONCE", device, button)).! == 0
  }
}
