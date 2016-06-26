import {Component} from "@angular/core"
import {ROUTER_DIRECTIVES} from "@angular/router"
import {FnbSettings} from "../util/settings"
import {AuthenticationService, AuthenticationState} from "../services/authentication.service"
import {Observable} from "rxjs/Observable"

class RegisterParams {
    public username: string = ""
    public email: string = ""
    public password: string = ""
}

@Component({
    selector: "fnb-register",
    templateUrl: "assets/frontend/register.html",
    directives: [ROUTER_DIRECTIVES]
})
export class RegisterComponent {

    constructor(public settings: FnbSettings,
                private authService: AuthenticationService) {
        this.authStatus = authService.authenticationStatus
        authService.failures.subscribe((msg: string) => { this.loginError = msg })
    }

    authStatus: Observable<AuthenticationState>

    public loginError: string = ""

    registerParams = new RegisterParams()

    register() {
        console.log("Trigger Register with " + this.registerParams.username + " : " + this.registerParams.email  + " : " + this.registerParams.password)
        this.authService.register(this.registerParams.username, this.registerParams.email, this.registerParams.password)
    }

    dismissError() {
        this.loginError = ""
    }

}