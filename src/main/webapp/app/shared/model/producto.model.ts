import { Size } from 'app/shared/model/enumerations/size.model';

export interface IProducto {
  id?: number;
  name?: string;
  description?: string;
  price?: number;
  size?: Size;
  imageContentType?: string;
  image?: any;
  productCategoryName?: string;
  productCategoryId?: number;
}

export class Producto implements IProducto {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string,
    public price?: number,
    public size?: Size,
    public imageContentType?: string,
    public image?: any,
    public productCategoryName?: string,
    public productCategoryId?: number
  ) {}
}
