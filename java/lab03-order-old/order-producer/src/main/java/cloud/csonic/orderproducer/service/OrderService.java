package cloud.csonic.orderproducer.service;

import cloud.csonic.orderlibrary.event.OrderEvent;
import org.springframework.kafka.support.SendResult;

public interface OrderService {
    void publishV1(OrderEvent orderEvent);
    SendResult<Integer, OrderEvent> publishV2(OrderEvent orderEvent);
    void publishV3(OrderEvent orderEvent);
}
