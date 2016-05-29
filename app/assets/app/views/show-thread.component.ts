import {Component} from "@angular/core"
import {ROUTER_DIRECTIVES, RouteSegment} from "@angular/router"
import {FnbSettings} from "../util/settings"
import {ApiResponse} from "../viewmodels/GeneralViewModels"
import {AlertComponent} from "ng2-bootstrap/components/alert"
import {ThreadService} from "../services/thread.service"
import {ShowThreadResult, ShowThreadPost, ShowThreadPostUpload} from "../viewmodels/ThreadsViewModels"

class ShowThreadAttachmentData {
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

class ShowThreadPostData {
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

class ShowThreadData {
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

@Component({
    selector: "fnb-show-thread",
    templateUrl: "assets/app/views/show-thread.html",
    directives: [ROUTER_DIRECTIVES, AlertComponent]
})
export class ShowThreadComponent {

    constructor(private routeSegment: RouteSegment,
                public settings: FnbSettings,
                private threadService: ThreadService) {
        console.log("-- Create ShowThreadComponent with id " + routeSegment.getParam("id"))
        this.id = routeSegment.getParam("id")
        this.load()
    }

    load() {
        this.threadService.loadThread(this.id).subscribe((result: ApiResponse<ShowThreadResult>) => {
            this.loaded = true
            if (result.success) {
                this.thread = new ShowThreadData(result.getResult())
                this.forumId = this.thread.forumId
                this.successful = true
            } else {
                console.warn("Unsuccessful request: ", result.toString())
                this.successful = false
                this.errorMessage = result.getError().error
            }
        })
    }

    showReply() {
        alert("Not Implemented yet")
    }

    public id: string
    public forumId: string = ""

    // TODO: Error Handling
    public loaded: boolean = false
    public successful: boolean = true
    public errorMessage: string = ""

    public thread: ShowThreadData = null

    // TODO: Permissions
    public permissionReply: boolean = true
    public permissionAvatar: boolean = true

}