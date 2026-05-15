import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * Paginador prev/next reutilizable.
 *
 * Uso:
 *   <app-pager
 *     [page]="page"
 *     [totalPages]="totalPages"
 *     (pageChange)="onPageChange($event)">
 *   </app-pager>
 */
@Component({
  selector: 'app-pager',
  templateUrl: './pager.component.html',
  styleUrls: ['./pager.component.css']
})
export class PagerComponent {

  @Input() page: number = 0;
  @Input() totalPages: number = 0;
  @Output() pageChange = new EventEmitter<number>();

  prev() {
    if (this.page > 0) this.pageChange.emit(this.page - 1);
  }

  next() {
    if (this.page < this.totalPages - 1) this.pageChange.emit(this.page + 1);
  }
}
