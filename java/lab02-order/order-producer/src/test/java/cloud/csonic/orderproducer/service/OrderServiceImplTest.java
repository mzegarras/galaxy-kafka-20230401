package cloud.csonic.orderproducer.service;

import cloud.csonic.orderlibrary.domain.Order;
import cloud.csonic.orderlibrary.event.OrderEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
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


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {


    @Mock
    KafkaTemplate<Integer, OrderEvent> kafkaTemplate;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void publishV4() throws ExecutionException, InterruptedException {

        //PREPARACIÓN DE DATA
        var order = Order.builder()
                .amout(100d)
                .customerId("CLI001")
                .build();

        var orderEvent = OrderEvent.builder()
                .eventId(1001)
                .order(order)
                .build();

        var producerRecord = new ProducerRecord<>("orders",orderEvent.getEventId(),orderEvent);
        var topicPartition = new TopicPartition("orders",1);
        var recordMetada = new RecordMetadata(topicPartition,1L,1,243L,1,2);
        var sendResult = new SendResult<>(producerRecord,recordMetada);

        SettableListenableFuture future = new SettableListenableFuture();
        future.set(sendResult);

        //CONFIGURACION DE MOCKS
        //when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        when(kafkaTemplate.send(producerRecord)).thenReturn(future);

        //EJECUTAR LÓGICA DE APLICACIÓN
        var listeneableFeature = orderService.publishV4(orderEvent);

        //VALIDACIONES
        var result = listeneableFeature.get();
        assertEquals(1,result.getRecordMetadata().partition());
        assertEquals(1001,result.getProducerRecord().key());


        verify(kafkaTemplate).send(producerRecord);
        verifyNoMoreInteractions(kafkaTemplate);
    }
}