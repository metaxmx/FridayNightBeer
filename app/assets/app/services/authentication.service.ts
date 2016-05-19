
import {AuthenticationStatus, AuthenticationStatusUser} from "../viewmodels/AuthenticationViewModels";
import {Injectable} from "angular2/core";

@Injectable()
export class AuthenticationService {

    private initialized = false

    private authStatus: AuthenticationStatus = {
        authenticated: false,
        globalPermissions: [],
    }

    refreshAuthentication(): void  {
        this.initialized = true
    }

    isInitialized() {
        return this.initialized
    }

    getAuthStatus() {
        return this.authStatus
    }

    login(username: String, password: String): Promise<AuthenticationStatus> {
        // Mock
        let mockStatus = <AuthenticationStatus> {
            authenticated: true,
            sessionId: "abc1234",
            globalPermissions: ["Forum", "Media"],
            user: <AuthenticationStatusUser> {
                id: "aaaaaaaa",
                username: "mustermann",
                displayName: "Musermann",
                fullName: "Max Mustermann"
            }
        }
        this.authStatus = mockStatus
        return Promise.resolve(this.authStatus)
    }

    logout(): Promise<AuthenticationStatus> {
        // Mock
        let mockStatus = <AuthenticationStatus> {
            authenticated: false,
            globalPermissions: [],
        }
        this.authStatus = mockStatus
        return Promise.resolve(this.authStatus)
    }

}