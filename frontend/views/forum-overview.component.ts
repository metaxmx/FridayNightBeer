import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings"
import {ForumService, OverviewForumCategory} from "../services/forum.service"

@Component({
    selector: "fnb-forum-overview",
    templateUrl: "forum-overview.component.html"
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