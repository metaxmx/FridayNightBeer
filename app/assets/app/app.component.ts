import {Component, OnInit,  ElementRef} from "@angular/core"
import {Routes, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from "@angular/router"
import {HTTP_PROVIDERS} from "@angular/http"
import {Observable} from "rxjs/Observable"

import {ForumOverviewComponent} from "./views/forum-overview.component"
import {ShowForumComponent} from "./views/show-forum.component"
import {ShowThreadComponent} from "./views/show-thread.component"
import {CreateThreadComponent} from "./views/create-thread.component"
import {RegisterComponent} from "./views/register.component"
import {LoginComponent} from "./views/login.component"
import {SettingsComponent} from "./views/settings.component"
import {MediaComponent} from "./views/media.component"
import {EventsComponent} from "./views/events.component"
import {UsersComponent} from "./views/users.component"
import {ShowUserComponent} from "./views/show-user.component"
import {AdminComponent} from "./views/admin.component"
import {HeaderComponent} from "./views/header.component"
import {FooterComponent} from "./views/footer.component"

import {AuthenticationService, AuthenticationState} from "./services/authentication.service"
import {FNB_SERVICE_PROVIDERS} from "./services/services"

import {FnbSettings} from "./util/settings"
import {FNB_UTILS_PROVIDERS} from "./util/utils"


@Component({
    selector: "fnb-app",
    templateUrl: "assets/app/app.html",
    directives: [HeaderComponent, FooterComponent, ROUTER_DIRECTIVES],
    providers: [HTTP_PROVIDERS, ROUTER_PROVIDERS, FNB_SERVICE_PROVIDERS, FNB_UTILS_PROVIDERS],
})
@Routes([
    {   path: "/",                      component: ForumOverviewComponent   },
    {   path: "/forum/:id",             component: ShowForumComponent       },
    {   path: "/thread/:id",            component: ShowThreadComponent      },
    {   path: "/forum/:id/newthread",   component: CreateThreadComponent    },
    {   path: "/register",              component: RegisterComponent        },
    {   path: "/login",                 component: LoginComponent           },
    {   path: "/settings",              component: SettingsComponent        },
    {   path: "/media",                 component: MediaComponent           },
    {   path: "/events",                component: EventsComponent          },
    {   path: "/users",                 component: UsersComponent           },
    {   path: "/user/:id",              component: ShowUserComponent        },
    {   path: "/admin",                 component: AdminComponent           }
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