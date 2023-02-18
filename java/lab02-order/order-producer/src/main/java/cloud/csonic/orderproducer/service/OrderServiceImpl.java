package cloud.csonic.orderproducer.service;

import cloud.csonic.orderlibrary.event.OrderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {


    //@Autowired
    //private KafkaTemplate<Integer,OrderEvent> kafkaTemplate;
    private final KafkaTemplate<Integer,OrderEvent> kafkaTemplate;
    /*public OrderServiceImpl(KafkaTemplate<Integer,OrderEvent> kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
    }*/

    private final String topicName="orders";

    @Override
    public void publish(OrderEvent orderEvent) {

        var key = orderEvent.getEventId();
        var response = kafkaTemplate.sendDefault(key,orderEvent);

        response.addCallback(new ListenableFutureCallback<SendResult<Integer, OrderEvent>>() {
            @Override
            public void onFailure(Throwable ex) {

            }

            @Override
            public void onSuccess(SendResult<Integer, OrderEvent> result) {

                handleOk(key,orderEvent,result);
            }
        });
    }

    @Override
    public void publishV2(OrderEvent orderEvent) {
        var key = orderEvent.getEventId();
        var response = kafkaTemplate.send(topicName,key,orderEvent);
        response.addCallback(new ListenableFutureCallback<SendResult<Integer, OrderEvent>>() {
            @Override
            public void onFailure(Throwable ex) {

            }

            @Override
            public void onSuccess(SendResult<Integer, OrderEvent> result) {

                handleOk(key,orderEvent,result);
            }
        });
    }

    @Override
    public void publishV3(OrderEvent orderEvent) {

        var key = orderEvent.getEventId();

        var record = buildRecord(key,orderEvent,topicName);

        var response = kafkaTemplate.send(record);
        response.addCallback(new ListenableFutureCallback<SendResult<Integer, OrderEvent>>() {
            @Override
            public void onFailure(Throwable ex) {

            }

            @Override
            public void onSuccess(SendResult<Integer, OrderEvent> result) {

                handleOk(key,orderEvent,result);
            }
        });

    }

    @Override
    public ListenableFuture<SendResult<Integer, OrderEvent>> publishV4(OrderEvent orderEvent) {
        var key = orderEvent.getEventId();

        var record = buildRecord(key,orderEvent,topicName);

        var response = kafkaTemplate.send(record);
        response.addCallback(new ListenableFutureCallback<SendResult<Integer, OrderEvent>>() {
            @Override
            public void onFailure(Throwable ex) {

            }

            @Override
            public void onSuccess(SendResult<Integer, OrderEvent> result) {

                handleOk(key,orderEvent,result);
            }
        });

        return response;
    }

    private ProducerRecord<Integer,OrderEvent> buildRecord(Integer key, OrderEvent orderEvent, String topicName) {
        return new ProducerRecord<>(topicName,null,key,orderEvent,null);
    }

    private void handleOk(Integer key, OrderEvent orderEvent, SendResult<Integer, OrderEvent> result) {
        log.info("mensaje enviado: key:{} - value:{} - partition: {} - offset {}",key,orderEvent,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }
}
