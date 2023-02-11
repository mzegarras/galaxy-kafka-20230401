package cloud.csonic.orderconsumer.consumers;

import cloud.csonic.orderconsumer.data.OrderEntity;
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

        log.info("mensaje recibido");

        var entity = new OrderEntity();
        entity.setId(orderEvent.getOrder().getId());
        entity.setAmout(orderEvent.getOrder().getAmout());
        entity.setCustomerId(orderEvent.getOrder().getCustomerId());
        orderRepository.save(entity);

        log.info(orderEvent.toString());

    }
}
