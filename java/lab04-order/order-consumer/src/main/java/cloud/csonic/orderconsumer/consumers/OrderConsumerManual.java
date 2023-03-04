package cloud.csonic.orderconsumer.consumers;

import cloud.csonic.orderconsumer.data.OrderEntity;
import cloud.csonic.orderconsumer.respository.OrderRepository;
import cloud.csonic.orderconsumer.service.OrderService;
import cloud.csonic.orderlibrary.event.OrderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@Slf4j
@AllArgsConstructor
public class OrderConsumerManual implements AcknowledgingMessageListener<Integer,OrderEvent> {

    private final OrderService orderService;

    @Override
    @KafkaListener(topics = {"orders"})
    @RetryableTopic(
            attempts = "4",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            backoff = @Backoff(delay = 10000, multiplier = 5.0)
            //backoff = @Backoff(delay = 5000),
            //autoCreateTopics = "false",
           // exclude = {SerializationException.class, DeserializationException.class}

    )
    public void onMessage(ConsumerRecord<Integer, OrderEvent> consumerRecord, Acknowledgment acknowledgment) {
        log.info("ConsumerRecord Manual: {}",consumerRecord);
        var orderEvent = consumerRecord.value();
        orderService.processEvent(orderEvent);
        acknowledgment.acknowledge(); //mensaje procesado ok
    }



}
