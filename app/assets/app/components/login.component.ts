import {Component} from "angular2/core"
import {FnbSettings} from "../util/settings"
import {ROUTER_DIRECTIVES} from "angular2/router"
import {AuthenticationService, AuthenticationState} from "../services/authentication.service"
import {Observable} from "rxjs/Observable"


class LoginParams {
    public username: string = "Please enter username"
    public password: string = ""
}

@Component({
    selector: "fnb-login",
    templateUrl: "assets/app/components/login.html",
    directives: [ROUTER_DIRECTIVES]
})
export class LoginComponent {

    constructor(public settings: FnbSettings,
                private authService: AuthenticationService) {
        this.authStatus = authService.authenticationStatus
    }

    authStatus: Observable<AuthenticationState>

    loginParams = new LoginParams()

    login() {
        console.log("Trigger Login with " + this.loginParams.username + " : " + this.loginParams.password)
        this.authService.login(this.loginParams.username, this.loginParams.password)
        this.loginParams.username = ""
        this.loginParams.password = ""
    }

    logout() {
        console.log("Trigger Logout")
        this.authService.logout()
    }

}