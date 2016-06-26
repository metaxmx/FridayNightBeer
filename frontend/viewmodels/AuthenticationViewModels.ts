import {ApiResult} from "./GeneralViewModels"

/**
 * ViewModels for Authentication
 */

export class LoginRequest {
    constructor(public username:string,
                public password:string) {
    }
}

export interface AuthenticationStatusUser {
    id:string
    username:string
    email:string
    displayName:string
    fullName?:string
    avatar?:string,
    groups?:Array<string>
}

export interface AuthenticationStatus {
    authenticated:boolean
    sessionId?:string
    user?:AuthenticationStatusUser
    globalPermissions:Array<string>
}

export interface AuthenticationResult extends ApiResult {
    authenticationStatus:AuthenticationStatus
}

export interface LoginResult extends AuthenticationResult {
}

export interface LogoutResult extends AuthenticationResult {
}

export interface GetAuthenticationStatusResult extends AuthenticationResult {
}

export class RegisterRequest {
    constructor(public username:string,
                public email:string,
                public password:string) {
    }
}

export interface RegisterResult extends AuthenticationResult {
}