package cloud.csonic.orderproducer.service;

import cloud.csonic.orderlibrary.domain.Order;
import cloud.csonic.orderlibrary.event.OrderEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Mock
    KafkaTemplate<Integer,OrderEvent> kafkaTemplate;

    @InjectMocks
    OrderServiceImpl orderService;


    @Test
    void publishV1() throws ExecutionException, InterruptedException {
        var order = Order.builder()
                .id("ORD001")
                .customerId("C001")
                .amout(101d)
                .build();

        var orderEvent = OrderEvent
                .builder()
                .order(order)
                .build();

        SettableListenableFuture future = new SettableListenableFuture();

        var producerRecord = new ProducerRecord<>("orders", orderEvent.getEventId(), orderEvent);
        var topicPartition = new TopicPartition("orders",1);
        var recordMetadata = new RecordMetadata(topicPartition,1L,1,234L,1,2);
        var sendResult = new SendResult<>(producerRecord,recordMetadata);
        future.set(sendResult);

        //when
        //when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        when(kafkaTemplate.send(producerRecord)).thenReturn(future);
        var listenableFeature = orderService.publishV4(orderEvent);

        //then
        var result = listenableFeature.get();
        assertEquals(1,result.getRecordMetadata().partition());

        var orderEventResult = result.getProducerRecord().value();
        assertNotNull(orderEventResult);
        assertEquals("ORD001",orderEventResult.getOrder().getId());

        verify(kafkaTemplate).send(isA(ProducerRecord.class));
        //verify(objectMapper,times(2)).writeValueAsString(isA(LibraryEvent.class));
        verifyNoMoreInteractions(kafkaTemplate);



    }
}