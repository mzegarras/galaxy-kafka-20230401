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
    public String id;
    public String customerId;
    public double amout;


}
