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
    children: [
      {
        path: 'search',
        loadComponent: () => import('./texts').then(m => m.Texts)
      },
      {
        path: ':textId',
        loadComponent: () => import('./view/view').then(m => m.View)
      },
      {
        path: ':textId/view',
        loadComponent: () => import('./view/view').then(m => m.View)
      },
      {
        path: ':textId/delete',
        loadComponent: () => import('./delete/delete').then(m => m.Delete)
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'search'
      },
      {
        path: '**',
        loadComponent: () => import('@/shared/not-found').then(m => m.NotFoundComponent)
      }
    ]
  }
];