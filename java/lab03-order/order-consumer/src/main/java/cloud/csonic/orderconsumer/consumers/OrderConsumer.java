package cloud.csonic.orderconsumer.consumers;

import cloud.csonic.orderlibrary.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderConsumer {

    @KafkaListener(topics = {"orders"})
   public void processMessage(ConsumerRecord<String,OrderEvent> record){
        log.info("record: {}",record);
   }

}
