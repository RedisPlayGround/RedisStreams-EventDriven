package me.saechimdaeki.paymentservice

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamListener
import org.springframework.stereotype.Component

@Component
class OrderEventStreamListener(
    private val redisTemplate: StringRedisTemplate
) : StreamListener<String, MapRecord<String, String, String>> {

    private val log = LoggerFactory.getLogger(this::class.java)

    var paymentProcessId = 0

    override fun onMessage(message: MapRecord<String, String, String>) {
        val map = message.value
        val userId = map["userId"] ?: ""
        val productId = map["productId"] ?: ""
        val price = map["price"] ?: ""

        //결제 관련 로직 처리
        //...

        val paymentIdStr = paymentProcessId++.toString()


        // 결제 완료 이벤트 발행

       val fieldMap = mutableMapOf<String, String>()
        fieldMap["userId"] = userId
        fieldMap["productId"] = productId
        fieldMap["price"] = price
        fieldMap["paymentProcessId"] = paymentIdStr

        redisTemplate.opsForStream<String,Any>().add("payment-events",fieldMap)

        log.info("[Order consumed] Created payment: {}", paymentIdStr)
    }
}