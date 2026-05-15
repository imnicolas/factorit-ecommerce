import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';
import { DialogService } from '../../services/dialog.service';
import { Cart, CartItem, EstadoCarrito } from '../../models/cart.model';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-cart-detail',
  templateUrl: './cart-detail.component.html',
  styleUrls: ['./cart-detail.component.css']
})
export class CartDetailComponent implements OnInit, OnDestroy {

  cart: Cart | null = null;
  products: Product[] = [];
  errorMessage: string = '';
  successMessage: string = '';

  selectedProductId: number | null = null;
  selectedQuantity: number = 1;

  // Flag para evitar doble-delete cuando el usuario elimina manualmente o finaliza.
  private alreadyHandled = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private cartService: CartService,
    private productService: ProductService,
    private dialog: DialogService
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loadCart(Number(idParam));
    }
    this.loadProducts();
  }

  ngOnDestroy(): void {
    // Si el usuario se mueve de ruta y el carrito sigue ABIERTO (no finalizó la compra
    // ni lo eliminó manualmente), se destruye automáticamente.
    if (!this.alreadyHandled && this.cart && this.cart.state === EstadoCarrito.ABIERTO) {
      this.cartService.eliminarCarrito(this.cart.id).subscribe({
        error: (err) => console.error('Error auto-eliminando carrito no finalizado', err)
      });
    }
  }

  loadCart(id: number) {
    this.cartService.getCarrito(id).subscribe({
      next: (cart) => {
        this.cart = cart;
      },
      error: (err) => {
        this.errorMessage = 'No se pudo cargar el carrito. ' + (err.error?.message || err.message);
      }
    });
  }

  loadProducts() {
    this.productService.getProducts().subscribe(prods => this.products = prods);
  }

  addProduct() {
    this.clearMessages();
    if (!this.cart || !this.selectedProductId) return;

    this.cartService.agregarProducto(this.cart.id, this.selectedProductId, this.selectedQuantity).subscribe({
      next: (cart) => {
        this.cart = cart;
        this.successMessage = 'Producto agregado.';
      },
      error: (err) => {
        this.errorMessage = 'Error al agregar producto: ' + (err.error?.message || err.message);
      }
    });
  }

  /**
   * Editar la cantidad de un producto desde la tabla.
   * Calcula la diferencia con la cantidad actual y dispara agregar o reducir según corresponda.
   * Si la nueva cantidad es <= 0, el backend (reducirCantidadProducto) elimina el producto.
   */
  onQuantityChange(item: CartItem, event: Event) {
    this.clearMessages();
    if (!this.cart) return;

    const input = event.target as HTMLInputElement;
    const nueva = Number(input.value);

    if (isNaN(nueva) || nueva < 0) {
      input.value = String(item.cantidad);
      return;
    }
    const diff = nueva - item.cantidad;
    if (diff === 0) return;

    const obs = diff > 0
      ? this.cartService.agregarProducto(this.cart.id, item.productoId, diff)
      : this.cartService.eliminarProducto(this.cart.id, item.productoId, Math.abs(diff));

    obs.subscribe({
      next: (cart) => {
        this.cart = cart;
      },
      error: (err) => {
        this.errorMessage = 'Error al actualizar la cantidad: ' + (err.error?.message || err.message);
        input.value = String(item.cantidad);
      }
    });
  }

  removeProduct(productoId: number) {
    this.clearMessages();
    if (!this.cart) return;

    this.cartService.eliminarProducto(this.cart.id, productoId).subscribe({
      next: (cart) => {
        this.cart = cart;
        this.successMessage = 'Producto eliminado.';
      },
      error: (err) => {
        this.errorMessage = 'Error al eliminar producto: ' + (err.error?.message || err.message);
      }
    });
  }

  finalizePurchase() {
    this.clearMessages();
    if (!this.cart) return;

    this.cartService.finalizarCompra(this.cart.id).subscribe({
      next: (cart) => {
        this.cart = cart;
        this.alreadyHandled = true;
        this.successMessage = '¡Compra finalizada con éxito!';
      },
      error: (err) => {
        this.errorMessage = 'Error al finalizar la compra: ' + (err.error?.message || err.message);
      }
    });
  }

  async deleteCart() {
    this.clearMessages();
    if (!this.cart) return;

    const ok = await this.dialog.confirm(
      'Esta acción es irreversible. El carrito y sus productos se eliminarán por completo.',
      { title: '¿Eliminar carrito?', confirmLabel: 'Eliminar', cancelLabel: 'Cancelar', danger: true }
    );
    if (!ok) return;

    this.cartService.eliminarCarrito(this.cart.id).subscribe({
      next: () => {
        this.alreadyHandled = true;
        this.router.navigate(['/carts']);
      },
      error: (err) => {
        this.errorMessage = 'Error al eliminar el carrito: ' + (err.error?.message || err.message);
      }
    });
  }

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  get isReadonly(): boolean {
    return this.cart?.state === EstadoCarrito.COMPLETADO || this.cart?.state === EstadoCarrito.DESTRUIDO;
  }
}
