import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/client.model';

/**
 * Autocomplete reutilizable de clientes con paginación (10 resultados, alfabético).
 *
 * Uso:
 *   <app-client-autocomplete
 *     [selected]="selectedClient"
 *     (selectedChange)="onClientChange($event)"
 *     [placeholder]="'Buscar cliente...'"
 *     [showClear]="true">
 *   </app-client-autocomplete>
 */
@Component({
  selector: 'app-client-autocomplete',
  templateUrl: './client-autocomplete.component.html',
  styleUrls: ['./client-autocomplete.component.css']
})
export class ClientAutocompleteComponent implements OnInit {

  @Input() selected: Client | null = null;
  @Input() placeholder: string = 'Buscar por nombre, apellido o DNI...';
  @Input() showClear: boolean = false;
  @Output() selectedChange = new EventEmitter<Client | null>();

  searchTerm: string = '';
  clientes: Client[] = [];
  showDropdown = false;
  loading = false;

  private search$ = new Subject<string>();

  constructor(private clientService: ClientService) { }

  ngOnInit(): void {
    // Si el padre ya pasó un cliente preseleccionado, mostrarlo
    if (this.selected) {
      this.searchTerm = this.formatLabel(this.selected);
    }

    this.search$
      .pipe(debounceTime(300), distinctUntilChanged(), switchMap(term => {
        this.loading = true;
        return this.clientService.searchClientes(term, 0, 10);
      }))
      .subscribe({
        next: (page) => {
          this.clientes = page.content;
          this.loading = false;
        },
        error: () => {
          this.clientes = [];
          this.loading = false;
        }
      });
  }

  onFocus() {
    this.showDropdown = true;
    if (this.clientes.length === 0) {
      this.search$.next(this.searchTerm);
    }
  }

  onChange(value: string) {
    this.searchTerm = value;
    if (this.selected) {
      this.selected = null;
      this.selectedChange.emit(null);
    }
    this.showDropdown = true;
    this.search$.next(value);
  }

  onSelect(client: Client) {
    this.selected = client;
    this.searchTerm = this.formatLabel(client);
    this.showDropdown = false;
    this.selectedChange.emit(client);
  }

  onBlur() {
    setTimeout(() => this.showDropdown = false, 150);
  }

  clear() {
    this.selected = null;
    this.searchTerm = '';
    this.selectedChange.emit(null);
  }

  private formatLabel(c: Client): string {
    return `${c.nombre} ${c.apellido} (DNI: ${c.dni})`;
  }
}
