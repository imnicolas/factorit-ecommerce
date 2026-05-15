import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { CartHistoryComponent } from './components/cart-history/cart-history.component';
import { CartDetailComponent } from './components/cart-detail/cart-detail.component';
import { VipClientsComponent } from './components/vip-clients/vip-clients.component';
import { DialogComponent } from './components/dialog/dialog.component';
import { ClientAutocompleteComponent } from './components/client-autocomplete/client-autocomplete.component';
import { PagerComponent } from './components/pager/pager.component';

@NgModule({
  declarations: [
    AppComponent,
    CartHistoryComponent,
    CartDetailComponent,
    VipClientsComponent,
    DialogComponent,
    ClientAutocompleteComponent,
    PagerComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
