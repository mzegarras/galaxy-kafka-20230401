package cloud.csonic.orderconsumer.consumers;

import cloud.csonic.orderconsumer.respository.OrderRepository;
import cloud.csonic.orderlibrary.event.OrderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class OrderConsumer {


    private final OrderRepository orderRepository;

    @KafkaListener(topics = "orders",groupId = "consumerjava01")
    public void processMessage(OrderEvent orderEvent){

        //orderRepository.save()
        log.info(orderEvent.toString());

    }
}
