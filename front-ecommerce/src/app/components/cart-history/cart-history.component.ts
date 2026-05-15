import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { Cart } from '../../models/cart.model';
import { Client } from '../../models/client.model';
import { ANIOS, MESES } from '../../models/meses.constant';

@Component({
  selector: 'app-cart-history',
  templateUrl: './cart-history.component.html',
  styleUrls: ['./cart-history.component.css']
})
export class CartHistoryComponent implements OnInit {

  // Filtros del listado
  carritoId: number | null = null;
  filterClient: Client | null = null;
  queryAnio: number = new Date().getFullYear();
  queryMes: number = new Date().getMonth() + 1;

  // Resultados
  carritos: Cart[] = [];
  loading = false;
  errorMessage = '';

  // Paginación
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;

  // Modal Crear carrito
  showCreateModal = false;
  createSelectedClient: Client | null = null;
  createFechaSimulada: string = '';
  createError = '';

  anios = ANIOS;
  meses = MESES;

  constructor(private cartService: CartService, private router: Router) { }

  ngOnInit(): void {
    this.buscar();
  }

  buscar() {
    this.page = 0;
    this.fetchCarritos();
  }

  private fetchCarritos() {
    this.errorMessage = '';
    this.loading = true;

    this.cartService.buscarCarritos(
      {
        carritoId: this.carritoId,
        clienteId: this.filterClient?.id ?? null,
        anio: this.queryAnio,
        mes: this.queryMes
      },
      this.page,
      this.size
    ).subscribe({
      next: (resp) => {
        this.carritos = resp.content;
        this.totalElements = resp.totalElements;
        this.totalPages = resp.totalPages;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al buscar carritos: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  onPageChange(newPage: number) {
    this.page = newPage;
    this.fetchCarritos();
  }

  onFilterClientChange(c: Client | null) {
    this.filterClient = c;
  }

  verDetalle(cart: Cart) {
    this.router.navigate(['/carts', cart.id]);
  }

  estadoClass(estado: string): string {
    return 'estado-' + estado.toLowerCase();
  }

  // ─── Modal Crear carrito ──────────────────────────────────────────

  openCreateModal() {
    this.createSelectedClient = null;
    this.createFechaSimulada = new Date().toISOString().substring(0, 10);
    this.createError = '';
    this.showCreateModal = true;
  }

  closeCreateModal() {
    this.showCreateModal = false;
  }

  onCreateClientChange(c: Client | null) {
    this.createSelectedClient = c;
  }

  crearCarrito() {
    this.createError = '';
    if (!this.createSelectedClient) {
      this.createError = 'Seleccioná un cliente del listado';
      return;
    }
    const fecha = this.createFechaSimulada ? this.createFechaSimulada : undefined;
    this.cartService.crearCarrito(this.createSelectedClient.id, fecha).subscribe({
      next: (cart) => {
        this.showCreateModal = false;
        this.router.navigate(['/carts', cart.id]);
      },
      error: (err) => {
        this.createError = 'Error al crear el carrito: ' + (err.error?.message || err.message);
      }
    });
  }
}
