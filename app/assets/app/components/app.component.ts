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
        this.authStatus = this.authService.authenticationStatus
    }

    authStatus: Observable<AuthenticationState>

    refreshAuthentication() {
        this.authService.refreshAuthentication()
    }

}