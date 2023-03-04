package cloud.csonic.orderconsumer.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("orders")
@Getter
@Setter
public class OrderEntity {
    @Id
    private String id;
    private String customerId;
    private double amout;


}
