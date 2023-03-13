package me.saechimdaeki.paymentservice

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.Subscription
import java.time.Duration

@Configuration
class RedisStreamConfig(
    private val orderEventStramListener : OrderEventStreamListener
) {

    @Bean
    fun subscription(factory: RedisConnectionFactory) : Subscription {
        val options =
            StreamMessageListenerContainer.StreamMessageListenerContainerOptions
            .builder()
            .pollTimeout(Duration.ofSeconds(1))
            .build()

        val listenerContainer = StreamMessageListenerContainer.create(factory, options)


        val subscription = listenerContainer.receiveAutoAck(
            Consumer.from("payment-service-group", "instance-1"),
            StreamOffset.create("order-events", ReadOffset.lastConsumed()), orderEventStramListener)

        listenerContainer.start()
        return subscription
    }
}