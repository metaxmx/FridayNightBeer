import {Component, OnInit, ElementRef} from "@angular/core"
import {Observable} from "rxjs/Observable"

import {AuthenticationService, AuthenticationState} from "./services/authentication.service"

import {FnbSettings} from "./util/settings"

@Component({
    selector: "fnb-app",
    templateUrl: "assets/frontend/app.html",
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