import {Component, OnInit} from "angular2/core"
import {RouteConfig, ROUTER_PROVIDERS} from "angular2/router"
import {ForumOverviewComponent} from "./forum-overview.component"
import {ShowForumComponent} from "./show-forum.component"
import {ShowThreadComponent} from "./show-thread.component"
import {AuthenticationService, AuthenticationState} from "../services/authentication.service"
import {HTTP_PROVIDERS} from "angular2/http"
import {FNB_SERVICE_PROVIDES} from "../services/services"
import {Observable} from "rxjs/Observable"

@Component({
    selector: "fnb-app",
    templateUrl: "assets/app/components/app.html",
    directives: [ForumOverviewComponent, ShowForumComponent, ShowThreadComponent],
    providers: [HTTP_PROVIDERS, ROUTER_PROVIDERS, FNB_SERVICE_PROVIDES]
})
@RouteConfig([
    {
        path: "/",
        name: "Forums",
        component: ForumOverviewComponent
    },
    {
        path: "/forum/:id",
        name: "Forum",
        component: ShowForumComponent
    },
    {
        path: "/thread/:id",
        name: "Thread",
        component: ShowThreadComponent
    }
])
export class AppComponent implements OnInit {

    constructor(public authService: AuthenticationService) {
    }

    ngOnInit() {
        console.log("Initialized AppComponent")
        console.log("AuthService:", this.authService)
        this.authService.authenticationStatus.subscribe((s: AuthenticationState) => console.log(s))
        // this.perm.subscribe((b: string) => console.log("perm", b))
        this.authStatus = this.authService.authenticationStatus
        this.perm = this.authService.authenticationStatus.map((s: AuthenticationState) => s.globalPermissions.join(","))
        this.username = this.authService.authenticationStatus.map((s: AuthenticationState) => s.user.username + "foo")
    }

    authStatus: Observable<AuthenticationState>
    perm: Observable<string>
    username: Observable<string>

    checkStatus() {
        console.log("OnClick")
        this.authService.refreshAuthentication()
    }

    // get authStatus(): Observable<boolean> {
    //   return this.authService.authenticationStatus.map((s: AuthenticationState) => s.loggedIn)
    // }

    // get username() {
    //     console.log("GET USERNAME")
    //     return this.authService.authenticationStatus.map((s: AuthenticationState) => s.user.username)
    // }

    // get perm(): Observable<string> {
    //     console.log("GET Perm")
    //     return this.authService.authenticationStatus.map((s: AuthenticationState) => s.globalPermissions.join(","))
    // }

}