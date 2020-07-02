import { OrderItemStatus } from 'app/shared/model/enumerations/order-item-status.model';

export interface IOrderItem {
  id?: number;
  quantity?: number;
  totalPrice?: number;
  status?: OrderItemStatus;
  productoName?: string;
  productoId?: number;
  orderCode?: string;
  orderId?: number;
}

export class OrderItem implements IOrderItem {
  constructor(
    public id?: number,
    public quantity?: number,
    public totalPrice?: number,
    public status?: OrderItemStatus,
    public productoName?: string,
    public productoId?: number,
    public orderCode?: string,
    public orderId?: number
  ) {}
}
