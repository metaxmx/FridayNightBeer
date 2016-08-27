import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings"
import {AuthenticationService, AuthenticationState} from "../services/authentication.service"
import {Observable} from "rxjs/Observable"

@Component({
    selector: "fnb-header",
    templateUrl: "assets/frontend/header.html"
})
export class HeaderComponent {

    constructor(public settings: FnbSettings,
                private authService: AuthenticationService) {
        this.headerAuthState = authService.authenticationStatus.map(HeaderComponent.mapToHeaderPermissions)
    }

    public headerAuthState: Observable<HeaderAuthState>

    private static mapToHeaderPermissions(authState: AuthenticationState): HeaderAuthState {
        return new HeaderAuthState(authState.loggedIn, authState.globalPermissions)
    }

}

class HeaderAuthState {
    constructor(public loggedIn: boolean,
                private permissions: Array<string>) {
        this.permissionForum = false
        this.permissionMedia = false
        this.permissionEvents = false
        this.permissionMembers = false
        this.permissionAdmin = false
        for(let permission of permissions) {
            switch(permission) {
                case "Forums":
                    this.permissionForum = true
                    break
                case "Media":
                    this.permissionMedia = true
                    break
                case "Events":
                    this.permissionEvents = true
                    break
                case "Members":
                    this.permissionMembers = true
                    break
                case "Admin":
                    this.permissionAdmin = true
                    break
                default:
                    break
            }
        }
    }
    public permissionForum: boolean
    public permissionMedia: boolean
    public permissionEvents: boolean
    public permissionMembers: boolean
    public permissionAdmin: boolean
}