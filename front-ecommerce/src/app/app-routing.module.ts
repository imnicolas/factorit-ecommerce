import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CartDetailComponent } from './components/cart-detail/cart-detail.component';
import { CartHistoryComponent } from './components/cart-history/cart-history.component';
import { VipClientsComponent } from './components/vip-clients/vip-clients.component';

const routes: Routes = [
  { path: '', redirectTo: 'carts', pathMatch: 'full' },
  { path: 'carts', component: CartHistoryComponent },
  { path: 'carts/:id', component: CartDetailComponent },
  { path: 'vip-clients', component: VipClientsComponent },
  { path: '**', redirectTo: 'carts' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
