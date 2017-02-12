import com.google.inject.AbstractModule
import java.time.Clock
import javax.inject.Provider

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.{DefaultProcessLogger, TestableProcessLogger}
import play.api.libs.concurrent.AkkaGuiceSupport
import services._

import scala.sys.process.{ProcessCreation, ProcessLogger}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure() = {

    bind(classOf[ProcessCreation]).toInstance(scala.sys.process.Process)
    bind(classOf[TestableProcessLogger]).toProvider(new Provider[TestableProcessLogger] {
      override def get(): TestableProcessLogger = new DefaultProcessLogger
    })
    bind(classOf[LircParser]).to(classOf[DefaultLircParser])
  }

}
