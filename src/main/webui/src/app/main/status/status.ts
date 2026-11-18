/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { Info, HealthInfo, ApiService } from '@/shared/api';
import { TitleService } from '@/shared';
import { HealthStatusComponent } from './health-status';

@Component({
	selector: 'app-status',
	imports: [
		HealthStatusComponent,
	],
	templateUrl: './status.html',
	styleUrl: './status.css'
})
export class Status implements OnInit, OnDestroy {

	/**
	 * The informaiton of the started MOV.
	 */
	public info = signal<Info | null>(null);

	/**
	 * The informaiton of the started MOV.
	 */
	public health = signal<HealthInfo | null>(null);

	/**
	 * The identifier of the timer.
	 */
	private timeoutID: ReturnType<typeof setTimeout> | null = null;

	/**
	 *  Create the component.
	 */
	constructor(
		private title: TitleService,
		private api: ApiService
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.title.changeHeaderTitle($localize`:The header title for the status@@main_status_code_page-title:Status`);
		this.api.getHelp().subscribe(
			{
				next: info => this.info.set(info),
				error: error => console.error(error)
			}

		);
		this.updateHealth();

	}

	/**
	 * Called when has to update the health information.
	 */
	public updateHealth() {

		this.api.getHealth().subscribe(
			{
				next: health => {

					this.health.set(health);
					this.timeoutID = setTimeout(() => this.updateHealth(), 30000);

				},
				error: error => console.error(error)
			}
		)
	}

	/**
	 * Finalizes the component.
	 */
	ngOnDestroy(): void {

		if (this.timeoutID != null) {

			clearTimeout(this.timeoutID);
		}
	}

}
