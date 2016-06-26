import {Injectable} from "@angular/core"
import {HttpCommunicationService} from "./http-communication.service"
import {ApiResponse} from "../viewmodels/GeneralViewModels"
import {Observable} from "rxjs/Observable"
import {ShowThreadResult, ShowThreadPostUpload, ShowThreadPost} from "../viewmodels/ThreadsViewModels"
import {ApiLoader, ApiModelLoader} from "../util/ApiLoader"

export class ShowThreadAttachmentData {
    constructor(postId: string, attachment: ShowThreadPostUpload) {
        this.filename = attachment.filename
        this.size = attachment.size
        this.hits = attachment.hits
        this.url = `/uploads/${postId}}/${attachment.filename}}`
    }
    filename:string
    size:number
    hits:number
    url:string
}

export class ShowThreadPostData {
    constructor(post: ShowThreadPost) {
        this.id = post.id
        this.date = new Date(post.date)
        this.userId = post.user
        this.userName = post.userName
        this.userFullname = post.userFullname || ""
        this.userAvatar = post.userAvatar || ""
        this.hasAvatar = this.userAvatar.length > 0
        this.userAvatarUrl = (this.hasAvatar ? "/avatar/" + this.userId : "")
        this.content = post.content
        let att: Array<ShowThreadPostUpload> = post.uploads || []
        this.attachments = att.map((attachment: ShowThreadPostUpload) => new ShowThreadAttachmentData(post.id, attachment))
        this.hasAttachments = this.attachments.length > 0
    }
    id:string
    date:Date
    userId:string
    userName:string
    userFullname:string
    hasAvatar: boolean
    userAvatar:string
    userAvatarUrl:string
    content:string
    attachments:Array<ShowThreadAttachmentData>
    hasAttachments: boolean
}

export class ShowThreadData {
    constructor(showThreadResult: ShowThreadResult) {
        this.title = showThreadResult.title
        this.forumId = showThreadResult.forum
        this.forumTitle = showThreadResult.forumTitle
        this.posts = showThreadResult.posts.map((post: ShowThreadPost) => new ShowThreadPostData(post))
    }
    public forumId: string
    public forumTitle: string
    public title: string
    public posts: Array<ShowThreadPostData>
}

const showThreadApiUrl = "thread/"

@Injectable()
export class ThreadService {

    constructor(private httpService: HttpCommunicationService) {
        console.debug("Initialize Service ThreadService")
    }

    public loadThread(id: string): Observable<ApiResponse<ShowThreadResult>> {
        console.log("--- Send Show Thread Request (id = " + id + ")")
        return this.httpService.GET<ShowThreadResult>(showThreadApiUrl + id)
    }

    public createShowThreadStatus(id: string): ApiModelLoader<ShowThreadData> {
        return new ApiLoader<ShowThreadResult, ShowThreadData>(
            () => this.loadThread(id),
            (showThreadResult: ShowThreadResult) => new ShowThreadData(showThreadResult))
    }

}