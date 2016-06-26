import {Component, OnInit,  ElementRef} from "@angular/core"
import {ROUTER_DIRECTIVES} from "@angular/router"
import {HTTP_PROVIDERS} from "@angular/http"
import {Observable} from "rxjs/Observable"

import {HeaderComponent} from "./views/header.component"
import {FooterComponent} from "./views/footer.component"

import {AuthenticationService, AuthenticationState} from "./services/authentication.service"
import {FNB_SERVICE_PROVIDERS} from "./services/services"

import {FnbSettings} from "./util/settings"
import {FNB_UTILS_PROVIDERS} from "./util/utils"


@Component({
    selector: "fnb-app",
    templateUrl: "assets/app/app.html",
    directives: [HeaderComponent, FooterComponent, ROUTER_DIRECTIVES],
    providers: [HTTP_PROVIDERS, FNB_SERVICE_PROVIDERS, FNB_UTILS_PROVIDERS],
})
export class AppComponent implements OnInit {

    constructor(private elRef: ElementRef,
                private authService: AuthenticationService,
                private settings: FnbSettings) {
        this.initSettings()
    }

    private initSettings() {
        // Native element access required, as root element parameters
        // are not supported by Angular2, yet.
        this.settings.updateLogo(this.elRef.nativeElement.getAttribute("data-logo") || "")
        this.settings.updateSettings(this.elRef.nativeElement.getAttribute("data-settings") || "{}")
    }

    ngOnInit() {
        console.log("Initialized AppComponent")
        this.authStatus = this.authService.authenticationStatus
    }

    authStatus: Observable<AuthenticationState>

    refreshAuthentication() {
        this.authService.refreshAuthentication()
    }

}