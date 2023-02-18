package cloud.csonic.orderconsumer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public void process() {

    }
}
