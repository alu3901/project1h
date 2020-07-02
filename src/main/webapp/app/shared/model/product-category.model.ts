import { IProducto } from 'app/shared/model/producto.model';

export interface IProductCategory {
  id?: number;
  name?: string;
  description?: string;
  productos?: IProducto[];
}

export class ProductCategory implements IProductCategory {
  constructor(public id?: number, public name?: string, public description?: string, public productos?: IProducto[]) {}
}
