/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { TitleService } from '@/shared';
import { ApiService, ReceivedTextPage } from '@/shared/api';
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';

/**
 * Messages Component
 */
@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatTableModule,
    MatPaginatorModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    MatProgressBarModule,
  ],
  templateUrl: './texts.html',
  styleUrl: './texts.css',
})
export class Texts implements OnInit {

  /**
   * The sercvice to change the title of the app.
   */
  private title = inject(TitleService);

  /**
   * The sercvice to get messages from the backend.
   */
  private api = inject(ApiService);

  /**
   * The identifier of the timer.
   */
  private timeoutID: ReturnType<typeof setTimeout> | null = null;

  /**
   * The currently page shown.
   */
  private currentPage = signal<number>(0);

  /**
   * The list of texts currently shown.
   */
  public page = signal<ReceivedTextPage>(new ReceivedTextPage());

  /**
   * The columns to display in the table.
   */
  public displayedColumns: string[] = ['id', 'title', 'receivedAt', 'actions'];

  /**
   * Whether the data is currently updating.
   */
  public updating: boolean = false;

  /**
   * The index to the fisrt patient to return.
   */
  public pageIndex: number = 0;

  /**
   * The number maximum of patients to show.
   */
  public pageSize: number = 10;

  /**
   * Initialize the component.
   */
  ngOnInit(): void {

    this.title.changeHeaderTitle($localize`:The header title for the texts@@main_texts_page-title:Received Texts`);
    this.updateTexts();

  }


  /**
   * Called each time to update the available received texts.
   */
  private updateTexts(): void {

    if (this.timeoutID) {
      clearTimeout(this.timeoutID);
      this.timeoutID = null;
    }

    this.updating = true;
    var offset = this.pageIndex * this.pageSize;
    this.api.getReceivedTextPage(offset, this.pageSize).subscribe(
      {
        next: page => {
          this.page.set(page);
          this.updating = false;
        },
        error: error => {
          this.updating = false;
          console.error(error);
        }
      }
    );
  }


  /**
   * Manage an event of the paginator.
   */
  public handlePageEvent(event: PageEvent) {

    if (this.pageSize != event.pageSize) {

      var offset = this.pageSize * this.pageIndex;
      this.pageIndex = Math.floor(offset / event.pageSize);
      this.pageSize = event.pageSize;
      this.updateTexts();

    } else if (this.pageIndex != event.pageIndex) {

      this.pageIndex = event.pageIndex;
      this.updateTexts();
    }

  }
}