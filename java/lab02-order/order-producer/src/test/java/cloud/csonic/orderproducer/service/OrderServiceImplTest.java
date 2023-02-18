package cloud.csonic.orderproducer.service;

import cloud.csonic.orderlibrary.domain.Order;
import cloud.csonic.orderlibrary.event.OrderEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {


    @Mock
    KafkaTemplate<Integer, OrderEvent> kafkaTemplate;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void publishV4() {
        var order = Order.builder()
                .amout(100d)
                .customerId("CLI001")
                .build();

        var orderEvent = OrderEvent.builder()
                .eventId(1001)
                .order(order)
                .build();

    }
}