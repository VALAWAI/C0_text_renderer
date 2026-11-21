/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TitleService } from '@/shared';
import { ApiService, ReceivedText } from '@/shared/api';
import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MarkdownComponent } from 'ngx-markdown';

/**
 * View a received text.
 */
@Component({
  selector: 'app-view',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatIconModule,
    MatButtonModule,
    MatProgressBarModule,
    MarkdownComponent,
  ],
  templateUrl: './view.html',
  styleUrl: './view.css',
})
export class View implements OnInit {

  /**
   * The service to change the title of the app.
   */
  private title = inject(TitleService);

  /**
   * The service to get messages from the backend.
   */
  private api = inject(ApiService);

  /**
   * The active route to retrieve route parameters.
   */
  private route = inject(ActivatedRoute);

  /**
   * The DOM sanitizer to securely render HTML content.
   */
  private sanitizer = inject(DomSanitizer);

  /**
   * The received text to display.
   */
  public text = signal<ReceivedText | null>(null);

  /**
   * Whether the text is currently loading.
   */
  public loading = signal<boolean>(true);

  /**
   * Error message if loading fails.
   */
  public error = signal<string | null>(null);

  /**
   * Sanitized HTML for text/html mime type.
   */
  public safeHtml = computed<SafeHtml>(() => {
    const content = this.text()?.content || '';
    return this.sanitizer.bypassSecurityTrustHtml(content);
  });

  /**
   * Initialize the component.
   */
  ngOnInit(): void {

    this.title.changeHeaderTitle($localize`:The header title for the text view@@main_text_view_page-title:Text View`);

    this.route.paramMap.subscribe(params => {
      const idParam = params.get('textId');
      if (idParam) {
        const textId = Number(idParam);
        this.loadText(textId);
      }
    });

  }

  /**
   * Load the text from the API.
   */
  private loadText(id: number): void {

    this.loading.set(true);
    this.error.set(null);

    this.api.getReceivedText(id).subscribe({
      next: text => {
        this.text.set(text);
        this.loading.set(false);
        if (text.title) {
          this.title.changeHeaderTitle(text.title);
        }
      },
      error: err => {
        this.loading.set(false);
        this.error.set($localize`:Error message when text cannot be loaded@@main_text_view_error:Failed to load text.`);
        console.error(err);
      }
    });

  }

}
