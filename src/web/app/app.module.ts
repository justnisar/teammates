import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from './app.component';
import { AboutPageComponent } from './pages-static/about-page/about-page.component';
import { ContactPageComponent } from './pages-static/contact-page/contact-page.component';
import { FeaturesPageComponent } from './pages-static/features-page/features-page.component';
import { IndexPageComponent } from './pages-static/index-page/index-page.component';
import { PageNotFoundComponent } from './pages-static/page-not-found/page-not-found.component';
import { RequestPageComponent } from './pages-static/request-page/request-page.component';
import { TermsPageComponent } from './pages-static/terms-page/terms-page.component';
import { UsermapPageComponent } from './pages-static/usermap-page/usermap-page.component';

const routes: Routes = [
  {
    path: 'web',
    children: [
      {
        path: 'home',
        component: IndexPageComponent,
      },
      {
        path: 'request',
        component: RequestPageComponent,
      },
      {
        path: 'features',
        component: FeaturesPageComponent,
      },
      {
        path: 'about',
        component: AboutPageComponent,
      },
      {
        path: 'contact',
        component: ContactPageComponent,
      },
      {
        path: 'terms',
        component: TermsPageComponent,
      },
      {
        path: 'usermap',
        component: UsermapPageComponent,
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home',
      },
      {
        path: '**',
        component: PageNotFoundComponent,
      },
    ],
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'web',
  },
];

/**
 * Root module.
 */
@NgModule({
  declarations: [
    AppComponent,
    IndexPageComponent,
    FeaturesPageComponent,
    ContactPageComponent,
    AboutPageComponent,
    TermsPageComponent,
    RequestPageComponent,
    UsermapPageComponent,
    PageNotFoundComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgbModule,
    RouterModule.forRoot(routes),
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
