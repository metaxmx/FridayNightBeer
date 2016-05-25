import {Component, Input} from "angular2/core"
import {FnbSettings} from "../util/settings"
import {ROUTER_DIRECTIVES} from "angular2/router"
import {ForumService, OverviewForumCategory} from "../services/forum.service"
import {DatePipe} from "../pipes/localized-date.pipe"

@Component({
    selector: "fnb-forum-overview",
    templateUrl: "assets/app/components/forum-overview.html",
    directives: [ROUTER_DIRECTIVES],
    pipes: [DatePipe]
})
export class ForumOverviewComponent {

    constructor(public settings: FnbSettings,
                private forumService: ForumService) {
        forumService.forumOverviewData.subscribe((data: Array<OverviewForumCategory>) => {
            this.categories = data
            this.loaded = true
            this.successful = true
        })
    }

    refresh() {
        this.forumService.refreshOverview()
    }

    // TODO: Error Handling
    public loaded: boolean = false
    public successful: boolean = true
    public errorMessage: string = ""

    public categories: Array<OverviewForumCategory> = []

}