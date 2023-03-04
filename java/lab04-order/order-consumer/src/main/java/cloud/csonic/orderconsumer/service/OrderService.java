package cloud.csonic.orderconsumer.service;

import cloud.csonic.orderlibrary.event.OrderEvent;

public interface OrderService {

     void processEvent(OrderEvent orderEvent);


}
