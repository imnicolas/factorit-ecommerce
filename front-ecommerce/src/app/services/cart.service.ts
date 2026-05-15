import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cart } from '../models/cart.model';
import { PageResponse } from '../models/page.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private apiUrl = `${environment.apiUrl}/api/carritos`;

  constructor(private http: HttpClient) { }

  crearCarrito(clienteId: number, fechaSimulada?: string): Observable<Cart> {
    let params = new HttpParams();
    if (fechaSimulada) {
      params = params.set('fechaSimulada', fechaSimulada);
    }
    return this.http.post<Cart>(this.apiUrl, { clienteId }, { params });
  }

  getCarrito(id: number): Observable<Cart> {
    return this.http.get<Cart>(`${this.apiUrl}/${id}`);
  }

  eliminarCarrito(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  agregarProducto(carritoId: number, productoId: number, cantidad: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/${carritoId}/productos`, { productoId, cantidad });
  }

  eliminarProducto(carritoId: number, productoId: number, cantidad?: number): Observable<Cart> {
    let params = new HttpParams();
    if (cantidad) {
      params = params.set('cantidad', cantidad.toString());
    }
    return this.http.delete<Cart>(`${this.apiUrl}/${carritoId}/productos/${productoId}`, { params });
  }

  finalizarCompra(carritoId: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/${carritoId}/finalizar`, {});
  }

  buscarCarritos(
    filtros: { carritoId?: number | null; clienteId?: number | null; anio?: number | null; mes?: number | null },
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<Cart>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (filtros.carritoId != null) {
      params = params.set('carritoId', filtros.carritoId.toString());
    }
    if (filtros.clienteId != null) {
      params = params.set('clienteId', filtros.clienteId.toString());
    }
    if (filtros.anio != null) {
      params = params.set('anio', filtros.anio.toString());
    }
    if (filtros.mes != null) {
      params = params.set('mes', filtros.mes.toString());
    }
    return this.http.get<PageResponse<Cart>>(this.apiUrl, { params });
  }
}
