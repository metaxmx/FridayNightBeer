import {Component} from "angular2/core"
import {ROUTER_DIRECTIVES, RouteParams} from "angular2/router"
import {DatePipe} from "../pipes/localized-date.pipe"
import {FnbSettings} from "../util/settings"
import {ForumService} from "../services/forum.service"
import {ApiResponse} from "../viewmodels/GeneralViewModels"
import {ShowForumResult} from "../viewmodels/ForumViewModels"

@Component({
    selector: "fnb-show-forum",
    templateUrl: "assets/app/views/show-forum.html",
    directives: [ROUTER_DIRECTIVES],
    pipes: [DatePipe]
})
export class ShowForumComponent {

    constructor(private routeParams: RouteParams,
                public settings: FnbSettings,
                private forumService: ForumService) {
        console.log("-- Create ShowForumComponent with id " + routeParams.get("id"))
        this.id = routeParams.get("id")
        this.load()
    }

    load() {
        this.forumService.loadForum(this.id).subscribe((result: ApiResponse<ShowForumResult>) => {
            this.loaded = true
            if (result.success) {
                this.forum = result.getResult()
                this.successful = true
            } else {
                console.warn("Unsuccessful request: ", result.toString())
                this.successful = false
                this.errorMessage = result.toString()
                // this.failures.next(result.getError().error)
            }
        })
    }

    private id: string

    // TODO: Error Handling
    public loaded: boolean = false
    public successful: boolean = true
    public errorMessage: string = ""

    public forum: ShowForumResult = null

    // TODO: Permissions
    public permissionCreateThread: boolean = true

}