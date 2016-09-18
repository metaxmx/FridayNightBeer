import {
    LoginRequest,
    AuthenticationResult,
    GetAuthenticationStatusResult,
    LogoutResult,
    LoginResult, RegisterRequest
} from "../viewmodels/AuthenticationViewModels"
import {Injectable} from "@angular/core"
import {HttpCommunicationService} from "./http-communication.service"
import {BehaviorSubject} from "rxjs/BehaviorSubject"
import {Subject} from "rxjs/Subject"
import {ApiResponse} from "../viewmodels/GeneralViewModels"

class CheckStatusAction {}
class LoginAction {
    constructor(public loginName: string, public password: string) {}
}
class LogoutAction {}
class RegisterAction {
    constructor(public loginName: string, public email: string, public password: string) {}
}
type AuthenticationAction = CheckStatusAction | LoginAction | LogoutAction

export interface AuthenticationEvent {}
export class LoginEvent implements AuthenticationEvent {}
export class LogoutEvent implements AuthenticationEvent {}

export class AuthenticationUserData {
    constructor(public id: string,
                public username: string,
                public email: string,
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

    public globalPermissionNames: Set<String> = new Set(this.globalPermissions);
}

const emptyAuthenticationUserData = new AuthenticationUserData("", "", "", "", "", "")
const initialState: AuthenticationState = new AuthenticationState(false, false, [], emptyAuthenticationUserData)

const authApiUrl = "authentication"
const registerApiUrl = "register"

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

    public events = new Subject<AuthenticationEvent>()

    private static mapAuthenticationResult(result: AuthenticationResult): AuthenticationState {
        let userData: AuthenticationUserData = emptyAuthenticationUserData
        if (result.authenticationStatus.authenticated) {
            const user = result.authenticationStatus.user
            userData = new AuthenticationUserData(user.id, user.username, user.email, user.displayName, user.fullName, user.avatar)
        }
        return new AuthenticationState(true, result.authenticationStatus.authenticated, result.authenticationStatus.globalPermissions, userData)
    }

    private createReactiveChain(): void {
        const http = this.httpService
        const observable = this.dispatcher.switchMap((action: AuthenticationAction) => {
            if (action instanceof RegisterAction) {
                const registerRequest = new RegisterRequest(action.loginName, action.email, action.password)
                console.log("--- Send Register Request")
                return http.POST<LoginResult>(registerApiUrl, registerRequest)
            } else if (action instanceof LoginAction) {
                const loginRequest = new LoginRequest(action.loginName, action.password)
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
                let oldState: AuthenticationState = this.authenticationStatus.getValue()
                this.authenticationStatus.next(AuthenticationService.mapAuthenticationResult(result.getResult()))
                let newState: AuthenticationState = this.authenticationStatus.getValue()
                if (oldState.loggedIn && !newState.loggedIn) {
                    this.events.next(new LogoutEvent())
                } else if (!oldState.loggedIn && newState.loggedIn) {
                    this.events.next(new LoginEvent())
                }
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

    register(username:string, email:string, password:string):void {
        this.dispatcher.next(new RegisterAction(username, email, password))
    }

    logout(): void {
        this.dispatcher.next(new LogoutAction())
    }

}