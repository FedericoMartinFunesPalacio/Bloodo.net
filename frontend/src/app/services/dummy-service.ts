import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient} from '@angular/common/http';
import { enviroment } from '../../env/enviroments';
import {Dummy, RequestDummy} from '../models/dummy';

@Injectable({
  providedIn: 'root',
})
export class DummyService {
  //Usar HTTPClient para hacer peticiones a la API RESTful y utilizar observables con RxJS para manejar las respuestas asincrónicas. Implementar métodos para crear, leer y actualizar dummies, así como para formatear las fechas según el formato requerido por la API.
  private baseUrl = `${enviroment.apiUrlDummy}/dummy`;

  constructor(private http: HttpClient) {}

  getDummies(): Observable<Dummy[]> {
    // Implementar lógica para obtener la lista de dummies desde la API RESTful utilizando HTTPClient y devolver un observable con la respuesta.
    return this.http.get<Dummy[]>(this.baseUrl);
  }

  createDummy(dummy: RequestDummy): Observable<Dummy> {
    // Implementar lógica para crear un nuevo dummy en la API RESTful utilizando HTTPClient y devolver un observable con la respuesta.
    return this.http.post<Dummy>(this.baseUrl, dummy);
  }

  updateDummy(id: string, dummy: RequestDummy): Observable<Dummy> {
    // Implementar lógica para actualizar un dummy existente en la API RESTful utilizando HTTPClient y devolver un observable con la respuesta.
    return this.http.put<Dummy>(this.baseUrl+`/${id}`, dummy);
  }

  getDummyById(id: string): Observable<Dummy> {
    // Implementar lógica para obtener un dummy específico por su ID desde la API RESTful utilizando HTTPClient y devolver un observable con la respuesta.
    return this.http.get<Dummy>(this.baseUrl+`/${id}`);
  }
}
