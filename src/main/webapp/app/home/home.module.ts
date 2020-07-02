import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Project1HSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';

@NgModule({
  imports: [Project1HSharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent],
})
export class Project1HHomeModule {}
