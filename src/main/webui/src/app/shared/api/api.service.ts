/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { HttpClient, HttpErrorResponse, HttpParams } from "@angular/common/http";
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { Observable, catchError, of, throwError } from 'rxjs';
import { HealthInfo } from './health-info.model';
import { Info } from './info.model';
import { ReceivedTextPage } from "./received-text-page.model";
import { ReceivedText } from "./received-text.model";

/**
 * The service used to interact with the 
 */
@Injectable({
	providedIn: 'root'
})
export class ApiService {

	/**
	 * Create the service.
	 */
	constructor(
		private http: HttpClient
	) {

	}

	/**
	 * Return the URL to the specified path.
	 */
	protected url(path: string, params: any[] | null = null): string {

		let url: string = environment.componentUrl + path;
		if (params) {

			for (let param of params) {

				if (!url.endsWith('/')) {

					url += '/';
				}
				url += encodeURIComponent(param);
			}

		}

		return url;
	}

	/**
	 * Return the option to a HTTP with some parameters.
	 */
	protected optionsWithParams(params: any | null = null): object {

		if (params) {

			var httpParams = this.toHttpParams(params);
			return { params: httpParams };

		} else {

			return {};
		}
	}

	/**
	 * Convert an object to HTTP params.
	 */
	private toHttpParams(params: any) {

		var httpParams = new HttpParams();
		for (let key of Object.keys(params)) {

			if (
				typeof params[key] === 'string' || params[key] instanceof String
				|| typeof params[key] === 'boolean' || params[key] instanceof Boolean
				|| typeof params[key] === 'number' || params[key] instanceof Number
			) {

				httpParams = httpParams.append(key, String(params[key]));
			}
		}
		return httpParams;

	}

	/**
	 * Get the help information.
	 */
	public getHelp(): Observable<Info> {

		var url = this.url('/v1/help/info');
		return this.http.get<Info>(url);
	}

	/**
	 * Get the help information.
	 */
	public getHealth(): Observable<HealthInfo> {

		var url = this.url('/q/health/');
		return this.http.get<HealthInfo>(url).pipe(
			catchError((err: HttpErrorResponse) => {
				// Quarkus returns 503 with the valid HealthResponse JSON when status is DOWN
				if (err.status === 503 && err.error?.status === 'DOWN') {
					return of(err.error as HealthInfo);
				}
				return throwError(() => err);
			})
		);
	}

	/**
	 * Get a page with some received texts.
	 */
	public getReceivedTextPage(offset: number = 0, limit: number = 10): Observable<ReceivedTextPage> {

		var url = this.url('/v1/texts');
		return this.http.get<ReceivedTextPage>(url, this.optionsWithParams({ offset: offset, limit: limit }));
	}

	/**
	 * Get a received text.
	 */
	public getReceivedText(id: number): Observable<ReceivedText> {

		var url = this.url('/v1/texts/' + id);
		return this.http.get<ReceivedText>(url);
	}

	/**
	 * Delete a received text.
	 */
	public deleteReceivedText(id: number): Observable<void> {

		var url = this.url('/v1/texts/' + id);
		return this.http.delete<void>(url);
	}

}