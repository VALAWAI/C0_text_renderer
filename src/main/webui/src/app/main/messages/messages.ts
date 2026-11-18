/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Messages Component
 */
@Component({
    selector: 'app-messages',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './messages.html',
    styleUrl: './messages.css',
})
export class Messages {
}