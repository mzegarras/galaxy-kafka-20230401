package cloud.csonic.orderconsumer.service;

import cloud.csonic.orderconsumer.data.OrderEntity;
import cloud.csonic.orderconsumer.respository.OrderRepository;
import cloud.csonic.orderlibrary.event.OrderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public void processEvent(OrderEvent orderEvent) {
        switch (orderEvent.getType()){
            case NEW:
                save(orderEvent);
                break;
            case UPDATE:
                //validate
                validate(orderEvent);
                save(orderEvent);
                break;
        }
    }

    private void validate(OrderEvent orderEvent) {
        if(orderEvent.getEventId()==null){
            throw new IllegalArgumentException("Order EventId es empty.");
        }
        var orderDB = orderRepository.findById(orderEvent.getOrder().getId());
        if(!orderDB.isPresent()){
            throw new IllegalArgumentException("Order EventId is not valid.");
        }

        log.info("Validation ok");
    }

    private void save(OrderEvent orderEvent) {
        var entity = new OrderEntity();
        entity.setId(orderEvent.getOrder().getId());
        entity.setAmout(orderEvent.getOrder().getAmout());
        entity.setCustomerId(orderEvent.getOrder().getCustomerId());
        orderRepository.save(entity);
    }
}
