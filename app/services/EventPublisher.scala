package services

import javax.inject.Inject

import akka.NotUsed
import akka.stream.scaladsl.Source
import models.BusinessEvent
import play.api.libs.iteratee.Concurrent
import play.api.libs.streams.Streams

trait EventPublisher {
  def publish(event: BusinessEvent): Unit
  def subscribe: Source[BusinessEvent, NotUsed]
}

/**
  * Implementation using {{{Concurrent.broadcast}}}
  */
class ConcurrentBroadcastEventPublisher @Inject()() extends EventPublisher {

  val (eventsOut, eventsChannel) = Concurrent.broadcast[BusinessEvent]

  override def publish(event: BusinessEvent): Unit = eventsChannel.push(event)

  override def subscribe: Source[BusinessEvent, NotUsed] = Source
    .fromPublisher(Streams.enumeratorToPublisher(eventsOut))
}
