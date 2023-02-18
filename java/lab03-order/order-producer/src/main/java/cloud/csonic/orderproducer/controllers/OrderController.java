package cloud.csonic.orderproducer.controllers;


import cloud.csonic.orderlibrary.domain.Order;
import cloud.csonic.orderlibrary.event.EventType;
import cloud.csonic.orderlibrary.event.OrderEvent;
import cloud.csonic.orderproducer.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String saveOrder(){
        var order = Order.builder()
                .amout(100d)
                .customerId("C001")
                .amout(101d)
                .build();

        var orderEvent = OrderEvent
                .builder()
                .order(order)
                .build();

        orderService.publishV1(orderEvent);

        return "ok";
    }

    @PostMapping
    public ResponseEntity<OrderEvent> postOrder(@RequestBody OrderEvent orderEvent){

        //orderService.publishV1(orderEvent);
        /*
        var rp = orderService.publishV2(orderEvent);
        log.info("Send Result is {}",rp.toString());
         */
        //orderService.publishV1(orderEvent);
        orderEvent.setType(EventType.NEW);
        orderService.publishV3(orderEvent);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(orderEvent);

    }

    @PutMapping()
    public ResponseEntity<?> putOrder(@RequestBody OrderEvent orderEvent){
        if(orderEvent.getEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Id");
        }
        orderEvent.setType(EventType.UPDATE);
        orderService.publishV3(orderEvent);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(orderEvent);
    }
}
