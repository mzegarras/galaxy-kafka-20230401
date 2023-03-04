package cloud.csonic.orderlibrary.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private String id;
    private String customerId;
    private Double amout;
}
