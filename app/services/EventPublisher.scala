package services

import javax.inject.Inject

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.google.inject.Singleton
import models.BusinessEvent
import play.api.libs.iteratee.Concurrent
import play.api.libs.streams.Streams

case class PublishableResult[RESULT, EVENT <: BusinessEvent](result: RESULT, events: Seq[EVENT])
case object PublishableResult {
  def apply[RESULT, EVENT <: BusinessEvent](res: RESULT, ev: EVENT): PublishableResult[RESULT, EVENT] = {
    PublishableResult(res, Seq(ev))
  }
}

trait EventPublisher {
  def publish[T <: BusinessEvent](event: T): T
  def subscribe: Source[BusinessEvent, NotUsed]

  def publishEventsAndReturnResult[RESULT, EVENT <: BusinessEvent](result: PublishableResult[RESULT, EVENT]): RESULT = {
    result.events.foreach(publish(_))
    result.result
  }
}

/**
  * Implementation using {{{Concurrent.broadcast}}}
  */
@Singleton
class ConcurrentBroadcastEventPublisher @Inject()() extends EventPublisher {

  val (eventsOut, eventsChannel) = Concurrent.broadcast[BusinessEvent]

  override def publish[T <: BusinessEvent](event: T): T = {
    eventsChannel.push(event)
    event
  }

  override def subscribe: Source[BusinessEvent, NotUsed] = Source
    .fromPublisher(Streams.enumeratorToPublisher(eventsOut))
}
