package cloud.csonic.orderlibrary.event;

import cloud.csonic.orderlibrary.domain.Order;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Integer eventId;
    private Order order;

    private EventType type;

}
