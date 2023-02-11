package cloud.csonic.orderproducer.controllers;


import cloud.csonic.orderlibrary.domain.Order;
import cloud.csonic.orderlibrary.event.OrderEvent;
import cloud.csonic.orderproducer.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String hello(){

        var order = Order.builder()
                .amout(100d)
                .customerId("CLI001")
                .build();

        var orderEvent = OrderEvent.builder()
                .eventId(1001)
                .order(order)
                .build();

        orderService.publishV2(orderEvent);

        return "ok";
    }

    @PostMapping
    public ResponseEntity<OrderEvent> postOrder(@RequestBody OrderEvent orderEvent){

        orderService.publishV2(orderEvent);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(orderEvent);
    }


}
