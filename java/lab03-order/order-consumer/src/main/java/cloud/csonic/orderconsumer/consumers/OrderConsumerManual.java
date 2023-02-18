package cloud.csonic.orderconsumer.consumers;

import cloud.csonic.orderlibrary.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderConsumerManual implements AcknowledgingMessageListener<String, OrderEvent> {

    @Override
    public void onMessage(ConsumerRecord<String, OrderEvent> consumerRecord, Acknowledgment acknowledgment) {
        log.info("commit manual record {}",consumerRecord);
        acknowledgment.acknowledge(); //mensaje procesado
    }
}
