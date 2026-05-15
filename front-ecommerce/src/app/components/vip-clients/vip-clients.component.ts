import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/client.model';
import { PageResponse } from '../../models/page.model';
import { MESES } from '../../models/meses.constant';

type VipTab = 'actuales' | 'nuevos' | 'perdidos';

@Component({
  selector: 'app-vip-clients',
  templateUrl: './vip-clients.component.html',
  styleUrls: ['./vip-clients.component.css']
})
export class VipClientsComponent implements OnInit {

  activeTab: VipTab = 'actuales';
  clients: Client[] = [];
  errorMessage = '';
  loading = false;

  queryAnio: number = new Date().getFullYear();
  queryMes: number = new Date().getMonth() + 1;

  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;

  meses = MESES;

  constructor(private clientService: ClientService) { }

  ngOnInit(): void {
    this.loadData();
  }

  selectTab(tab: VipTab) {
    this.activeTab = tab;
    this.page = 0;
    this.loadData();
  }

  applyFilters() {
    this.page = 0;
    this.loadData();
  }

  onPageChange(newPage: number) {
    this.page = newPage;
    this.loadData();
  }

  loadData() {
    this.errorMessage = '';
    this.clients = [];
    this.loading = true;

    let obs: Observable<PageResponse<Client>>;
    if (this.activeTab === 'actuales') {
      obs = this.clientService.getVipClients(this.queryAnio, this.queryMes, this.page, this.size);
    } else if (this.activeTab === 'nuevos') {
      obs = this.clientService.getNewVipClients(this.queryAnio, this.queryMes, this.page, this.size);
    } else {
      obs = this.clientService.getLostVipClients(this.queryAnio, this.queryMes, this.page, this.size);
    }

    obs.subscribe({
      next: (resp) => {
        this.clients = resp.content;
        this.totalElements = resp.totalElements;
        this.totalPages = resp.totalPages;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar clientes: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  get sectionTitle(): string {
    const mesLabel = this.meses.find(m => m.value === this.queryMes)?.label ?? this.queryMes;
    if (this.activeTab === 'actuales') return `Clientes VIP de ${mesLabel} ${this.queryAnio}`;
    return this.activeTab === 'nuevos'
      ? `Nuevos VIP de ${mesLabel} ${this.queryAnio}`
      : `Dejaron de ser VIP en ${mesLabel} ${this.queryAnio}`;
  }

  get sectionHint(): string {
    if (this.activeTab === 'actuales') {
      return 'Clientes cuya suma de compras completadas en este mes supera los $10.000.';
    }
    if (this.activeTab === 'nuevos') {
      return 'Clientes que pasaron a ser VIP en este mes (no lo eran el mes anterior).';
    }
    return 'Clientes que dejaron de ser VIP en este mes (lo eran el mes anterior).';
  }
}
