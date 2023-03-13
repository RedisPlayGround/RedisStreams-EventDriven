package me.saechimdaeki.orderservice

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(
    private val redisTemplate: StringRedisTemplate
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/order")
    fun order(@RequestParam userId: String, productId: String, @RequestParam price: String): String {

        val fieldMap = mutableMapOf<String,String>()
        fieldMap["userId"]= userId
        fieldMap["productId"] = productId
        fieldMap["price"] = price

        redisTemplate.opsForStream<String,Any>().add("order-events",fieldMap)

        log.info("Order created")

        return "ok"
    }
}