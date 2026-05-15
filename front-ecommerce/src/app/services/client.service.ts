import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../models/client.model';
import { PageResponse } from '../models/page.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private apiUrl = `${environment.apiUrl}/api/clientes`;

  constructor(private http: HttpClient) { }

  searchClientes(search: string = '', page: number = 0, size: number = 10): Observable<PageResponse<Client>> {
    const params = new HttpParams()
      .set('search', search)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Client>>(this.apiUrl, { params });
  }

  /**
   * VIP vigentes paginados. Si se pasan anio y mes, el backend primero degrada
   * a los VIP que no compraron en ese mes (consulta = evaluación).
   */
  getVipClients(anio?: number, mes?: number, page: number = 0, size: number = 10): Observable<PageResponse<Client>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (anio != null && mes != null) {
      params = params.set('anio', anio.toString()).set('mes', mes.toString());
    }
    return this.http.get<PageResponse<Client>>(`${this.apiUrl}/vip`, { params });
  }

  getNewVipClients(anio: number, mes: number, page: number = 0, size: number = 10): Observable<PageResponse<Client>> {
    const params = new HttpParams()
      .set('anio', anio.toString())
      .set('mes', mes.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Client>>(`${this.apiUrl}/vip/nuevos`, { params });
  }

  getLostVipClients(anio: number, mes: number, page: number = 0, size: number = 10): Observable<PageResponse<Client>> {
    const params = new HttpParams()
      .set('anio', anio.toString())
      .set('mes', mes.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Client>>(`${this.apiUrl}/vip/perdieron`, { params });
  }
}
