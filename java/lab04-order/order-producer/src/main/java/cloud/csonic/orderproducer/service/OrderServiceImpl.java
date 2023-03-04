package cloud.csonic.orderproducer.service;

import cloud.csonic.orderlibrary.event.OrderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{

    //private final KafkaTemplate<String,String> kafkaTemplate;
    private final KafkaTemplate<Integer,OrderEvent> kafkaTemplate;
    private final String topic="orders";

    @Override
    public void publishV1(OrderEvent event) {

        var key = event.getEventId() ;

        var futureResponse = kafkaTemplate.sendDefault(event);

        futureResponse.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key,  event, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, OrderEvent> result) {
                handleOk(key,event,result);
            }
        });


    }



    private void handleFailure(Integer key, OrderEvent event, Throwable ex) {
        log.error("Error sending Message exception is {},value:{} - {}",
                key,
                event,
                ex.getMessage()
        );
    }

    private void handleOk(Integer key, OrderEvent event, SendResult<Integer, OrderEvent> result) {
        log.info("Message sent successfully key{},value:{} - {}",
                key,
                event,
                result.getRecordMetadata().partition()
        );

    }

    @Override
    public SendResult<Integer, OrderEvent> publishV2(OrderEvent event) {
        SendResult<Integer,OrderEvent> result = null;

        try {
            result = kafkaTemplate.sendDefault(event).get(1000 * 5, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public void publishV3(OrderEvent event) {
        var key = event.getEventId();

        var record = buildProduceRecord(key,event,topic);
        
        var futureResponse = kafkaTemplate.send(record);

        futureResponse.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key,  event, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, OrderEvent> result) {
                handleOk(key,event,result);
            }
        });
    }

    @Override
    public ListenableFuture<SendResult<Integer, OrderEvent>> publishV4(OrderEvent event) {

        var key = event.getEventId();

        var record = buildProduceRecord(key,event,topic);

        var futureResponse = kafkaTemplate.send(record);

        futureResponse.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key,  event, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, OrderEvent> result) {
                handleOk(key,event,result);
            }
        });

        return futureResponse;
    }

    private ProducerRecord<Integer,OrderEvent> buildProduceRecord(Integer key, OrderEvent event, String topic) {
        return new ProducerRecord<>(topic,null,key,event,null);
    }


}
