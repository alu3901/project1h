package es.fabio.service.mapper;


import es.fabio.domain.*;
import es.fabio.service.dto.OrderItemDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProductoMapper.class, ProductOrderMapper.class})
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.name", target = "productoName")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.code", target = "orderCode")
    OrderItemDTO toDto(OrderItem orderItem);

    @Mapping(source = "productoId", target = "producto")
    @Mapping(source = "orderId", target = "order")
    OrderItem toEntity(OrderItemDTO orderItemDTO);

    default OrderItem fromId(Long id) {
        if (id == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        return orderItem;
    }
}
