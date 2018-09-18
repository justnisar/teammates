import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { HttpRequestService } from './http-request.service';

/**
 * Handles user authentication.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private frontendUrl: string = environment.frontendUrl;

  constructor(private httpRequestService: HttpRequestService) {}

  /**
   * Gets the user authentication information.
   */
  getAuthUser(): Observable<any> {
    const params: object = { frontendUrl: this.frontendUrl };
    return this.httpRequestService.get('/auth', params);
  }

}
