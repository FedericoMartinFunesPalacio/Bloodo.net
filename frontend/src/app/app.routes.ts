import { Routes } from '@angular/router';
import { PrincipalDummyComponent } from './pages/principal-dummy-component/principal-dummy-component';


export const routes: Routes = [
  { path: '', component: PrincipalDummyComponent, pathMatch: 'full' },
  // Redirigir cualquier otra ruta a la página principal
  { path: '**', redirectTo: '' }
];
