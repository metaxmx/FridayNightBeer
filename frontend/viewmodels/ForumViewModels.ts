import {ApiResult} from "./GeneralViewModels"

/**
 * ViewModels for Forums Overview
 */

export interface ForumOverviewLastPost {
    id:string
    title:string
    user:string
    userName:string
    date:string
}

export interface ForumOverviewForum {
    id:string
    name:string
    url:string
    description?:string
    numThreads:number
    numPosts:number
    lastPost?:ForumOverviewLastPost
}

export interface ForumOverviewCategory {
    id:string
    name:string
    forums:Array<ForumOverviewForum>
}

export interface ForumOverviewResult extends ApiResult {
    categories:Array<ForumOverviewCategory>
}

export interface ShowForumPost {
    user:string
    userName:string
    date:string
}

export interface ShowForumThread {
    id:string
    title:string
    posts:number
    sticky:boolean
    firstPost:ShowForumPost
    latestPost:ShowForumPost
    status:string
}

export interface ShowForumResult extends ApiResult {
    id:string
    title:string
    threads:Array<ShowForumThread>
    permissions:Array<string>
}

export interface ShowForumHeadResult extends ApiResult {
    id:string
    title:string
    permissions:Array<string>
}