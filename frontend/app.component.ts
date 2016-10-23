import {Component, OnInit,  NgZone} from "@angular/core"
import {Observable} from "rxjs/Observable"

import {AuthenticationService, AuthenticationState} from "./services/authentication.service"

import {LanguageService} from "./services/language.service";

@Component({
    selector: "fnb-app",
    templateUrl: "app.component.html",
})
export class AppComponent implements OnInit {

    constructor(private authService: AuthenticationService,
                private languageService: LanguageService,
                private zone: NgZone) {
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