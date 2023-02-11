package cloud.csonic.orderproducer.service;

import cloud.csonic.orderlibrary.event.OrderEvent;

public interface OrderService {
    void publish(OrderEvent orderEvent);
}
