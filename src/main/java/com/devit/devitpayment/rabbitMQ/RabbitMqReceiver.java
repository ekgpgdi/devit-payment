package com.devit.devitpayment.rabbitMQ;

import com.devit.devitpayment.point.service.PointService;
import com.devit.devitpayment.rabbitMQ.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqReceiver implements RabbitListenerConfigurer {
    private final PointService pointService;

    public RabbitMqReceiver(PointService pointService) {
        this.pointService = pointService;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
    }

    // 소비할 큐를 지정
    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void receivedMessage(UserDto user) {
        log.info("User Details Received is.. " + user);
        pointService.initUser(user);
    }
}