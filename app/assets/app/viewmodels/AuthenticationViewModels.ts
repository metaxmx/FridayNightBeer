import {ApiResult} from "./GeneralViewModels"

/**
 * ViewModels for Authentication
 */

export interface LoginRequest {
    username:string
    password:string
}

export interface AuthenticationStatusUser {
    id:string
    username:string
    displayName:string
    fullName?:string
    avatar?:string,
    groups?:Array<string>
}

export interface AuthenticationStatus {
    authenticated:boolean
    sessionId?:String
    user?:AuthenticationStatusUser
    globalPermissions:Array<string>
}

export interface LoginResult extends ApiResult {
    authenticationStatus:AuthenticationStatus
}

export interface LogoutResult extends ApiResult {
    authenticationStatus:AuthenticationStatus
}

export interface GetAuthenticationStatusResult extends ApiResult {
    authenticationStatus:AuthenticationStatus
}