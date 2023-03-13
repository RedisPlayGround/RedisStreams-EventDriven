package me.saechimdaeki.notifiactionservice

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamListener
import org.springframework.stereotype.Component

@Component
class PaymentEventStreamListener: StreamListener<String, MapRecord<String, String, String>> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onMessage(message: MapRecord<String, String, String>) {
        val map = message.value
        val userId = map["userId"] ?: ""
        val paymentProcessId = map["paymentProcessId"] ?: ""

        // 결제 완료 건에 대해 SMS 발송 처리

        log.info("[Order consumed] userId : {} paymentProcessId : {}",userId,paymentProcessId)
    }
}