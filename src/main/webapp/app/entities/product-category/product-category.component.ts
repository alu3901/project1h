import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IProductCategory } from 'app/shared/model/product-category.model';
import { ProductCategoryService } from './product-category.service';
import { ProductCategoryDeleteDialogComponent } from './product-category-delete-dialog.component';

@Component({
  selector: 'jhi-product-category',
  templateUrl: './product-category.component.html',
})
export class ProductCategoryComponent implements OnInit, OnDestroy {
  productCategories?: IProductCategory[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected productCategoryService: ProductCategoryService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch =
      this.activatedRoute.snapshot && this.activatedRoute.snapshot.queryParams['search']
        ? this.activatedRoute.snapshot.queryParams['search']
        : '';
  }

  loadAll(): void {
    if (this.currentSearch) {
      this.productCategoryService
        .search({
          query: this.currentSearch,
        })
        .subscribe((res: HttpResponse<IProductCategory[]>) => (this.productCategories = res.body || []));
      return;
    }

    this.productCategoryService.query().subscribe((res: HttpResponse<IProductCategory[]>) => (this.productCategories = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInProductCategories();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IProductCategory): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInProductCategories(): void {
    this.eventSubscriber = this.eventManager.subscribe('productCategoryListModification', () => this.loadAll());
  }

  delete(productCategory: IProductCategory): void {
    const modalRef = this.modalService.open(ProductCategoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.productCategory = productCategory;
  }
}
