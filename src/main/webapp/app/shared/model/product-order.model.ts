import { Moment } from 'moment';
import { IOrderItem } from 'app/shared/model/order-item.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface IProductOrder {
  id?: number;
  placedDate?: Moment;
  status?: OrderStatus;
  code?: string;
  invoiceId?: number;
  orderItems?: IOrderItem[];
  customerEmail?: string;
  customerId?: number;
}

export class ProductOrder implements IProductOrder {
  constructor(
    public id?: number,
    public placedDate?: Moment,
    public status?: OrderStatus,
    public code?: string,
    public invoiceId?: number,
    public orderItems?: IOrderItem[],
    public customerEmail?: string,
    public customerId?: number
  ) {}
}
