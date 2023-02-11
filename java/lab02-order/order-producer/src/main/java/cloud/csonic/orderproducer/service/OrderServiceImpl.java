package cloud.csonic.orderproducer.service;

import cloud.csonic.orderlibrary.event.OrderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
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
        var response = kafkaTemplate.sendDefault(orderEvent);
        var key = orderEvent.getEventId();

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

    private void handleOk(Integer key, OrderEvent orderEvent, SendResult<Integer, OrderEvent> result) {
        log.info("mensaje enviado");
    }
}
