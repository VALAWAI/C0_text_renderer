/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TitleService } from '@/shared';
import { ApiService, ReceivedText } from '@/shared/api';
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

/**
 * Delete a received text.
 */
@Component({
  selector: 'app-delete',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
  ],
  templateUrl: './delete.html',
  styleUrl: './delete.css',
})
export class Delete implements OnInit {

  /**
   * The service to change the title of the app.
   */
  private title = inject(TitleService);

  /**
   * The service to interact with the backend API.
   */
  private api = inject(ApiService);

  /**
   * The active route to retrieve route parameters.
   */
  private route = inject(ActivatedRoute);

  /**
   * The router to navigate between pages.
   */
  private router = inject(Router);

  /**
   * The identifier of the text to delete.
   */
  public textId: number = 0;

  /**
   * The text to delete.
   */
  public text = signal<ReceivedText | null>(null);

  /**
   * Whether the text information is loading.
   */
  public loading = signal<boolean>(true);

  /**
   * Whether the delete request is in progress.
   */
  public deleting = signal<boolean>(false);

  /**
   * Error message if loading or deleting fails.
   */
  public error = signal<string | null>(null);

  /**
   * Initialize the component.
   */
  ngOnInit(): void {

    this.title.changeHeaderTitle($localize`:The header title for deleting a text@@main_texts_delete_page-title:Delete Text`);

    this.route.paramMap.subscribe(params => {
      const idParam = params.get('textId');
      if (idParam) {
        this.textId = Number(idParam);
        this.loadText();
      }
    });

  }

  /**
   * Load the text information to show confirmation.
   */
  private loadText(): void {

    this.loading.set(true);
    this.error.set(null);

    this.api.getReceivedText(this.textId).subscribe({
      next: text => {
        this.text.set(text);
        this.loading.set(false);
      },
      error: err => {
        this.loading.set(false);
        this.error.set($localize`:Error when loading text to delete@@main_texts_delete_load-error:Failed to load text information.`);
        console.error(err);
      }
    });

  }

  /**
   * Confirms and deletes the received text.
   */
  public confirmDelete(): void {

    this.deleting.set(true);
    this.error.set(null);

    this.api.deleteReceivedText(this.textId).subscribe({
      next: () => {
        this.router.navigate(['/main/texts']);
      },
      error: err => {
        this.deleting.set(false);
        this.error.set($localize`:Error when deleting text@@main_texts_delete_action-error:Failed to delete text.`);
        console.error(err);
      }
    });

  }

  /**
   * Cancel and go back to texts.
   */
  public cancel(): void {

    this.router.navigate(['/main/texts']);

  }

}
