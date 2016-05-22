import {
    LoginRequest,
    AuthenticationResult,
    GetAuthenticationStatusResult,
    LogoutResult,
    LoginResult
} from "../viewmodels/AuthenticationViewModels"
import {Injectable} from "angular2/core"
import {HttpCommunicationService} from "./http-communication.service"
import {BehaviorSubject} from "rxjs/BehaviorSubject"
import {Subject} from "rxjs/Subject"
import {ApiResponse} from "../viewmodels/GeneralViewModels"

class CheckStatusAction {}
class LoginAction {
    constructor(public loginName: string, public password: string) {}
}
class LogoutAction { }
type AuthenticationAction = CheckStatusAction | LoginAction | LogoutAction

export class AuthenticationUserData {
    constructor(public id: string,
                public username: string,
                public displayName: string,
                public fullName: string,
                public avatar: string) {
    }
}

export class AuthenticationState {
    constructor(public initialized: boolean,
                public loggedIn: boolean,
                public globalPermissions: Array<string>,
                public user: AuthenticationUserData) {
    }
}

const emptyAuthenticationUserData = new AuthenticationUserData("", "", "", "", "")
const initialState: AuthenticationState = <AuthenticationState> {
    initialized: false,
    loggedIn: false,
    globalPermissions: [],
    user: emptyAuthenticationUserData
}

const authApiUrl = "authentication"

@Injectable()
export class AuthenticationService {

    constructor(private httpService: HttpCommunicationService) {
        console.debug("Initialize Service AuthenticationService")
        this.createReactiveChain()
        this.refreshAuthentication()
    }

    private dispatcher = new Subject<AuthenticationAction>()

    public authenticationStatus = new BehaviorSubject<AuthenticationState>(initialState)

    public failures = new Subject<string>()

    private static mapAuthenticationResult(result: AuthenticationResult): AuthenticationState {
        let userData: AuthenticationUserData = emptyAuthenticationUserData
        if (result.authenticationStatus.authenticated) {
            const user = result.authenticationStatus.user
            userData = new AuthenticationUserData(user.id, user.username, user.displayName, user.fullName, user.avatar)
        }
        return new AuthenticationState(true, result.authenticationStatus.authenticated, result.authenticationStatus.globalPermissions, userData)
    }

    private createReactiveChain(): void {
        const http = this.httpService
        const observable = this.dispatcher.switchMap((action: AuthenticationAction) => {
            if (action instanceof LoginAction) {
                let loginRequest = new LoginRequest(action.loginName, action.password)
                console.log("--- Send Login Request")
                return http.POST<LoginResult>(authApiUrl, loginRequest)
            } else if (action instanceof LogoutAction) {
                console.log("--- Send Logout Request")
                return http.DELETE<LogoutResult>(authApiUrl)
            } else {
                console.log("--- Send Check Auth Status Request")
                return http.GET<GetAuthenticationStatusResult>(authApiUrl)
            }
        }).share()
        observable.subscribe((result: ApiResponse<AuthenticationResult>) => {
            if (result.success) {
                this.authenticationStatus.next(AuthenticationService.mapAuthenticationResult(result.getResult()))
            } else {
                console.warn("Unsuccessful request: ", result.toString())
                this.failures.next(result.getError().error)
            }
        })
    }

    refreshAuthentication(): void {
        this.dispatcher.next(new CheckStatusAction)
    }

    login(username:string, password:string):void {
         this.dispatcher.next(new LoginAction(username, password))
    }

    logout(): void {
        this.dispatcher.next(new LogoutAction())
    }

}