/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () => import('./main').then(c => c.Main),
        children: [
            {
                path: 'messages',
                loadChildren: () => import('./messages/messages.routes').then(m => m.routes)
            },
            {
                path: 'status',
                loadComponent: () => import('./status/status').then(m => m.Status)
            },
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'messages'
            },
            {
                path: '**',
                loadComponent: () => import('@/shared/not-found').then(m => m.NotFoundComponent)
            }
        ]
    }

];
