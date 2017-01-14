package io

import scala.collection.mutable.ListBuffer
import scala.sys.process.ProcessLogger

/**
  * Created by jimbo on 14/01/17.
  */
trait TestableProcessLogger {

  def processLogger: ProcessLogger
  def lines: List[String]
}

class DefaultProcessLogger extends TestableProcessLogger {

  var _lines = ListBuffer[String]()
  override def processLogger: ProcessLogger = ProcessLogger(out => _lines += out, err => _lines += err)

  override def lines: List[String] = _lines.toList
}
