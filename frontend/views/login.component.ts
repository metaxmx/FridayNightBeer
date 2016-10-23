import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings"
import {Router} from "@angular/router"
import {
    AuthenticationService, AuthenticationState, AuthenticationEvent,
    LogoutEvent, LoginEvent
} from "../services/authentication.service"
import {Observable} from "rxjs/Observable"
import {ForumService} from "../services/forum.service";


class LoginParams {
    public username: string = ""
    public password: string = ""
}

@Component({
    selector: "fnb-login",
    templateUrl: "login.component.html"
})
export class LoginComponent {

    constructor(public settings: FnbSettings,
                private authService: AuthenticationService,
                private forumService: ForumService,
                private router: Router) {
        this.authStatus = authService.authenticationStatus;
        authService.failures.subscribe((msg: string) => { this.loginError = msg });
        authService.events.subscribe((event: AuthenticationEvent) => {
           if (event instanceof LogoutEvent) {
               console.log("### Logout successful");
               this.forumService.refreshOverview();
           } else if (event instanceof LoginEvent) {
               console.log("### Login successful");
               this.forumService.refreshOverview();
               this.router.navigate(["/"]);
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