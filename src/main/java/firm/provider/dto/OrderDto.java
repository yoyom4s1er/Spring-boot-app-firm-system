package firm.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import firm.provider.model.Firm;
import firm.provider.model.Order;
import firm.provider.model.Product;
import firm.provider.utils.OperationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDto {

    private long id;

    private String firmName;

    private OperationType operationType;

    private long operationTargetId;

    private String operationTargetName;

    private LocalDateTime date;

    private float totalPrice;

    private List<Long> products = new ArrayList<>();

    public Order toOrder() {
        Order order = new Order();
        order.setId(id);
        order.setFirm(new Firm(firmName));
        order.setOperationType(operationType);
        order.setOperationTargetId(operationTargetId);
        order.setLocationTargetName(operationTargetName);
        order.setDate(date);
        order.setTotalPrice(totalPrice);
        List<Product> productList = new ArrayList<>();
        for (Long id:
             products) {
            productList.add(new Product(id));
        }
        order.setProducts(productList);

        return order;
    }

    public static OrderDto fromOrder(Order order) {
        OrderDto orderDto = new OrderDto();

        orderDto.setId(order.getId());
        orderDto.setFirmName(order.getFirm().getName());
        orderDto.setOperationType(order.getOperationType());
        orderDto.setOperationTargetId(order.getOperationTargetId());
        orderDto.setOperationTargetName(order.getLocationTargetName());
        orderDto.setDate(order.getDate());
        orderDto.setTotalPrice(order.getTotalPrice());
        for (Product product: order.getProducts()) {
            orderDto.getProducts().add(product.getId());
        }

        return orderDto;
    }
}
