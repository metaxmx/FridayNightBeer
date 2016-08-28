import {Component, OnInit, ElementRef, NgZone} from "@angular/core"
import {Observable} from "rxjs/Observable"

import {AuthenticationService, AuthenticationState} from "./services/authentication.service"

import {FnbSettings} from "./util/settings"
import {LanguageService} from "./services/language.service";

@Component({
    selector: "fnb-app",
    templateUrl: "assets/frontend/app.html",
})
export class AppComponent implements OnInit {

    constructor(private elRef: ElementRef,
                private authService: AuthenticationService,
                private languageService: LanguageService,
                private settings: FnbSettings,
                private zone: NgZone) {
        this.initSettings()
    }

    private initSettings() {
        // Native element access required, as root element parameters
        // are not supported by Angular2, yet.
        this.settings.updateLogo(this.elRef.nativeElement.getAttribute("data-logo") || "");
        this.settings.updateSettings(this.elRef.nativeElement.getAttribute("data-settings") || "{}");
    }

    ngOnInit() {
        console.log("Initialized AppComponent");
        this.authStatus = this.authService.authenticationStatus;
        this.languageService.init(() => {
            // Enforce re-rendering of whole Angular2 Page
            window.setTimeout(() => {
                this.zone.run(() => {
                    // This will trigger a re-render of the component associated with the zone, i.e. the app component
                });
            }, 100);
        });
    }

    authStatus: Observable<AuthenticationState>;

    refreshAuthentication() {
        this.authService.refreshAuthentication();
    }

}