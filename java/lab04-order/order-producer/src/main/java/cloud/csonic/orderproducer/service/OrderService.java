package cloud.csonic.orderproducer.service;

import cloud.csonic.orderlibrary.event.OrderEvent;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface OrderService {
    void publishV1(OrderEvent orderEvent);
    SendResult<Integer, OrderEvent> publishV2(OrderEvent orderEvent);
    void publishV3(OrderEvent orderEvent);

    ListenableFuture<SendResult<Integer, OrderEvent>> publishV4(OrderEvent orderEvent);
}
