import {NgModule} from '@angular/core'
import {BrowserModule} from '@angular/platform-browser'
import {RouterModule} from '@angular/router'
import {APP_ROUTES} from "./app.routes"

import {AppComponent}  from './app.component'
import {FNB_COMPONENTS} from "./views/components"
import {FNB_SERVICE_PROVIDERS} from "./services/services"
import {FNB_UTILS_PROVIDERS} from "./util/utils"
import {HttpModule} from "@angular/http"
import {FormsModule} from '@angular/forms'

@NgModule({
    imports: [
        BrowserModule,
        RouterModule.forRoot(APP_ROUTES),
        HttpModule,
        FormsModule
    ],
    declarations: [
        AppComponent,
        FNB_COMPONENTS
    ],
    providers: [
        FNB_SERVICE_PROVIDERS, FNB_UTILS_PROVIDERS
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}