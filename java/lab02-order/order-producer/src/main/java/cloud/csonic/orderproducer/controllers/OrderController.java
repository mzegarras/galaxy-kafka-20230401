package cloud.csonic.orderproducer.controllers;


import cloud.csonic.orderlibrary.domain.Order;
import cloud.csonic.orderlibrary.event.OrderEvent;
import cloud.csonic.orderproducer.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
                .eventId(null)
                .order(order)
                .build();

        orderService.publish(orderEvent);

        return "ok";
    }
}
