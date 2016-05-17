import {ApiResult} from "./GeneralViewModels"

/**
 * ViewModels for Threads
 */

export interface ShowThreadPostUpload {
    filename:string
    size:number
    hits:number
}

export interface ShowThreadPost {
    id:string
    date:Date
    user:string
    userName:string
    userFullname?:string
    userAvatar:boolean
    content:string
    uploads?:Array<ShowThreadPostUpload>
}

export interface ShowThreadResult extends ApiResult {
    id:string
    title:string
    forum:string
    forumTitle:string
    posts:Array<ShowThreadPost>
}

export interface CreateThreadRequest {
    title:string
    firstPostContent:string,
    sticky:boolean,
    close:boolean
}

export interface CreateThreadResult extends ApiResult {
    id:string
}

export interface CreatePostRequest {
    content:string
    makeSticky:boolean,
    close:boolean
}

export interface CreatePostResult extends ApiResult {

}