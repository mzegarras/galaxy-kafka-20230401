package cloud.csonic.orderconsumer.consumers;

import cloud.csonic.orderconsumer.data.OrderEntity;
import cloud.csonic.orderconsumer.respository.OrderRepository;
import cloud.csonic.orderlibrary.event.OrderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrderConsumerManual implements AcknowledgingMessageListener<String, OrderEvent> {
    private final OrderRepository orderRepository;

    @Override
    @KafkaListener(topics = {"orders"})
    public void onMessage(ConsumerRecord<String, OrderEvent> consumerRecord, Acknowledgment acknowledgment) {
        log.info("commit manual record {}",consumerRecord);
        log.info("hacer algo");

        //Necesito el número que viene se valida contra la base de datos
        // SI LA ORDEN NO EXISTE, LA REGISTRAN.
        // SI LA EXISTE LA IGNORAN.
        //acknowledgment.acknowledge(); //mensaje procesado

        log.info("ConsumerRecord Manual: {}",consumerRecord);

        var orderEvent = consumerRecord.value();

        var orderDB = orderRepository.findById(orderEvent.getOrder().getId());

        if(!orderDB.isPresent()){

            var entity = new OrderEntity();
            entity.setId(orderEvent.getOrder().getId());
            entity.setAmout(orderEvent.getOrder().getAmout());
            entity.setCustomerId(orderEvent.getOrder().getCustomerId());
            orderRepository.save(entity);
        }

        acknowledgment.acknowledge();

    }
}
