package cloud.csonic.orderconsumer.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("orders")
public class OrderEntity {
    @Id
    public String id;
    public String customerId;
    public double amout;


}
