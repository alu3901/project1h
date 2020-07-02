import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { Project1HSharedModule } from 'app/shared/shared.module';
import { Project1HCoreModule } from 'app/core/core.module';
import { Project1HAppRoutingModule } from './app-routing.module';
import { Project1HHomeModule } from './home/home.module';
import { Project1HEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    Project1HSharedModule,
    Project1HCoreModule,
    Project1HHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    Project1HEntityModule,
    Project1HAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  bootstrap: [MainComponent],
})
export class Project1HAppModule {}
