import {Component, OnInit, Input, ElementRef, provide} from "angular2/core"
import {RouteConfig, ROUTER_PROVIDERS} from "angular2/router"
import {ForumOverviewComponent} from "./forum-overview.component"
import {ShowForumComponent} from "./show-forum.component"
import {ShowThreadComponent} from "./show-thread.component"
import {AuthenticationService, AuthenticationState} from "../services/authentication.service"
import {HTTP_PROVIDERS} from "angular2/http"
import {FNB_SERVICE_PROVIDERS} from "../services/services"
import {Observable} from "rxjs/Observable"
import {FnbSettings} from "../util/settings"
import {FNB_UTILS_PROVIDERS} from "../util/utils"

@Component({
    selector: "fnb-app",
    templateUrl: "assets/app/components/app.html",
    directives: [ForumOverviewComponent, ShowForumComponent, ShowThreadComponent],
    providers: [HTTP_PROVIDERS, ROUTER_PROVIDERS, FNB_SERVICE_PROVIDERS, FNB_UTILS_PROVIDERS],
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