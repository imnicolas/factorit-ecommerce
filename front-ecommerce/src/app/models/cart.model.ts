import { Client } from './client.model';

export enum CartType {
  COMUN = 'COMUN',
  FECHA_ESPECIAL = 'FECHA_ESPECIAL',
  VIP = 'VIP'
}

export enum EstadoCarrito {
  ABIERTO = 'ABIERTO',
  COMPLETADO = 'COMPLETADO',
  DESTRUIDO = 'DESTRUIDO'
}

export interface CartItem {
  id: number;
  productoId: number;
  productoNombre: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Cart {
  id: number;
  tipo: CartType;
  state: EstadoCarrito;
  dateCreated: string;
  montoBruto: number;
  total: number;
  descuento: number;
  cliente: Client;
  detalles: CartItem[];
}
