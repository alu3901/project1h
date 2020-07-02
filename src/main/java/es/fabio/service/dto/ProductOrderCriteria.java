package es.fabio.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import es.fabio.domain.enumeration.OrderStatus;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link es.fabio.domain.ProductOrder} entity. This class is used
 * in {@link es.fabio.web.rest.ProductOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /product-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductOrderCriteria implements Serializable, Criteria {
    /**
     * Class for filtering OrderStatus
     */
    public static class OrderStatusFilter extends Filter<OrderStatus> {

        public OrderStatusFilter() {
        }

        public OrderStatusFilter(OrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderStatusFilter copy() {
            return new OrderStatusFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter placedDate;

    private OrderStatusFilter status;

    private StringFilter code;

    private LongFilter invoiceId;

    private LongFilter orderItemId;

    private LongFilter customerId;

    public ProductOrderCriteria() {
    }

    public ProductOrderCriteria(ProductOrderCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.placedDate = other.placedDate == null ? null : other.placedDate.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.invoiceId = other.invoiceId == null ? null : other.invoiceId.copy();
        this.orderItemId = other.orderItemId == null ? null : other.orderItemId.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
    }

    @Override
    public ProductOrderCriteria copy() {
        return new ProductOrderCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(InstantFilter placedDate) {
        this.placedDate = placedDate;
    }

    public OrderStatusFilter getStatus() {
        return status;
    }

    public void setStatus(OrderStatusFilter status) {
        this.status = status;
    }

    public StringFilter getCode() {
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public LongFilter getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(LongFilter invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LongFilter getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(LongFilter orderItemId) {
        this.orderItemId = orderItemId;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductOrderCriteria that = (ProductOrderCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(placedDate, that.placedDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(code, that.code) &&
            Objects.equals(invoiceId, that.invoiceId) &&
            Objects.equals(orderItemId, that.orderItemId) &&
            Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        placedDate,
        status,
        code,
        invoiceId,
        orderItemId,
        customerId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductOrderCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (placedDate != null ? "placedDate=" + placedDate + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (code != null ? "code=" + code + ", " : "") +
                (invoiceId != null ? "invoiceId=" + invoiceId + ", " : "") +
                (orderItemId != null ? "orderItemId=" + orderItemId + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
            "}";
    }

}
