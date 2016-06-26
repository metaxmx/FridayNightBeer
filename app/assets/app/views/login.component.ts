import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings"
import {ROUTER_DIRECTIVES, Router} from "@angular/router"
import {
    AuthenticationService, AuthenticationState, AuthenticationEvent,
    LogoutEvent, LoginEvent
} from "../services/authentication.service"
import {Observable} from "rxjs/Observable"


class LoginParams {
    public username: string = "Please enter username"
    public password: string = ""
}

@Component({
    selector: "fnb-login",
    templateUrl: "assets/app/views/login.html",
    directives: [ROUTER_DIRECTIVES]
})
export class LoginComponent {

    constructor(public settings: FnbSettings,
                private authService: AuthenticationService,
                private router: Router) {
        this.authStatus = authService.authenticationStatus
        authService.failures.subscribe((msg: string) => { this.loginError = msg })
        authService.events.subscribe((event: AuthenticationEvent) => {
           if (event instanceof LogoutEvent) {
               console.log("### Logout successful")
           } else if (event instanceof LoginEvent) {
               console.log("### Login successful")
               this.router.navigate(["/"])
           }
        })
    }

    authStatus: Observable<AuthenticationState>

    public loginError: string = ""

    public loginParams = new LoginParams()

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

    dismissError() {
        this.loginError = ""
    }

}