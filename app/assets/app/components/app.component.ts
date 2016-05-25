import {Component, OnInit,  ElementRef} from "angular2/core"
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from "angular2/router"
import {HTTP_PROVIDERS} from "angular2/http"
import {Observable} from "rxjs/Observable"

import {ForumOverviewComponent} from "./forum-overview.component"
import {ShowForumComponent} from "./show-forum.component"
import {ShowThreadComponent} from "./show-thread.component"
import {CreateThreadComponent} from "./create-thread.component"
import {RegisterComponent} from "./register.component"
import {LoginComponent} from "./login.component"
import {SettingsComponent} from "./settings.component"
import {MediaComponent} from "./media.component"
import {EventsComponent} from "./events.component"
import {MembersComponent} from "./members.component"
import {ShowUserComponent} from "./show-user.component"
import {AdminComponent} from "./admin.component"
import {HeaderComponent} from "./header.component"
import {FooterComponent} from "./footer.component"

import {AuthenticationService, AuthenticationState} from "../services/authentication.service"
import {FNB_SERVICE_PROVIDERS} from "../services/services"

import {FnbSettings} from "../util/settings"
import {FNB_UTILS_PROVIDERS} from "../util/utils"


@Component({
    selector: "fnb-app",
    templateUrl: "assets/app/components/app.html",
    directives: [HeaderComponent, FooterComponent, ROUTER_DIRECTIVES],
    providers: [HTTP_PROVIDERS, ROUTER_PROVIDERS, FNB_SERVICE_PROVIDERS, FNB_UTILS_PROVIDERS],
})
@RouteConfig([
    {
        path: "/",
        name: "Forums",
        component: ForumOverviewComponent,
        useAsDefault: true
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
    },
    {
        path: "/forum/:id/newthread",
        name: "CreateThread",
        component: CreateThreadComponent
    },
    {
        path: "/register",
        name: "Register",
        component: RegisterComponent
    },
    {
        path: "/login",
        name: "Login",
        component: LoginComponent
    },
    {
        path: "/settings",
        name: "Settings",
        component: SettingsComponent
    },
    {
        path: "/media",
        name: "Media",
        component: MediaComponent
    },
    {
        path: "/events",
        name: "Events",
        component: EventsComponent
    },
    {
        path: "/members",
        name: "Members",
        component: MembersComponent
    },
    {
        path: "/user/:id",
        name: "User",
        component: ShowUserComponent
    },
    {
        path: "/admin",
        name: "Admin",
        component: AdminComponent
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