package cloud.csonic.orderconsumer;

import cloud.csonic.orderconsumer.consumers.OrderConsumerManual;
import cloud.csonic.orderconsumer.respository.OrderRepository;
import cloud.csonic.orderconsumer.service.OrderService;
import cloud.csonic.orderlibrary.event.EventType;
import cloud.csonic.orderlibrary.event.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(topics = "orders", partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.template.default-topic=orders",
        "spring.mongodb.embedded.version=3.6.5"})
public class OrderConsumerIntegrationTests {

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    OrderConsumerManual orderConsumer;

    @SpyBean
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp(){
        assertNotNull(embeddedKafkaBroker);
        assertNotNull(kafkaTemplate);
        assertNotNull(endpointRegistry);
        assertNotNull(orderConsumer);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.getDb().drop();
    }

    @Test
    void publishMessage() throws ExecutionException, InterruptedException {
        endpointRegistry.getListenerContainers().forEach(p->{
            ContainerTestUtils.waitForAssignment(p,embeddedKafkaBroker.getPartitionsPerTopic());
        });

        var order = cloud.csonic.orderlibrary.domain.Order.builder()
                .amout(100d)
                .customerId("C001")
                .amout(101d)
                .build();

        var orderEvent = OrderEvent
                .builder()
                .type(EventType.NEW)
                .order(order)
                .build();


        kafkaTemplate.sendDefault(orderEvent).get();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        verify(orderConsumer,times(1)).onMessage(isA(ConsumerRecord.class),isA(Acknowledgment.class));
        verify(orderService,times(1)).processEvent(orderEvent);
        //orderService

        var listado = orderRepository.findAll();
       assertEquals(1,listado.size());
    }

}
