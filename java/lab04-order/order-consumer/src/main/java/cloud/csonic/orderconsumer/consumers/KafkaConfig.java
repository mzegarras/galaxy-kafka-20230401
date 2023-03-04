package cloud.csonic.orderconsumer.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.net.SocketTimeoutException;


@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler(){

        var fixedBackOff = new FixedBackOff(1000L, 3);
        var errorHandler = new DefaultErrorHandler(fixedBackOff);

        errorHandler
                .setRetryListeners(((record, ex, deliveryAttempt) -> {
                    log.info("Error Record intento Listener, Exception : {} , deliveryAttempt : {} "
                            ,ex.getMessage(), deliveryAttempt);
                }));

        //errorHandler.addRetryableExceptions(SocketTimeoutException.class);
        //errorHandler.addNotRetryableExceptions(NullPointerException.class);

        return  errorHandler;
    }


}
