package me.saechimdaeki.notifiactionservice

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamListener
import org.springframework.stereotype.Component

@Component
class OrderEventStreamListener: StreamListener<String, MapRecord<String, String, String>> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onMessage(message: MapRecord<String, String, String>) {
        val map = message.value
        val userId = map["userId"] ?: ""
        val productId = map["productId"] ?: ""

        // 주문 건에 대한 메일 발송 처리

        log.info("[Order consumed] userId : {} productId : {}",userId,productId)
    }
}