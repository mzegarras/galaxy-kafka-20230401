package cloud.csonic.orderconsumer.respository;

import cloud.csonic.orderconsumer.data.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<OrderEntity,String> {
}
