package es.fabio.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import es.fabio.domain.enumeration.OrderItemStatus;

/**
 * A DTO for the {@link es.fabio.domain.OrderItem} entity.
 */
public class OrderItemDTO implements Serializable {
    
    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal totalPrice;

    @NotNull
    private OrderItemStatus status;


    private Long productoId;

    private String productoName;

    private Long orderId;

    private String orderCode;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getProductoName() {
        return productoName;
    }

    public void setProductoName(String productoName) {
        this.productoName = productoName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long productOrderId) {
        this.orderId = productOrderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String productOrderCode) {
        this.orderCode = productOrderCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItemDTO)) {
            return false;
        }

        return id != null && id.equals(((OrderItemDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", totalPrice=" + getTotalPrice() +
            ", status='" + getStatus() + "'" +
            ", productoId=" + getProductoId() +
            ", productoName='" + getProductoName() + "'" +
            ", orderId=" + getOrderId() +
            ", orderCode='" + getOrderCode() + "'" +
            "}";
    }
}
