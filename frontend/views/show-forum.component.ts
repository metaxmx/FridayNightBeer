import {Component, OnInit, OnDestroy} from "@angular/core"
import {ActivatedRoute} from "@angular/router"
import {FnbSettings} from "../util/settings"
import {ForumService} from "../services/forum.service"
import {ApiResponse} from "../viewmodels/GeneralViewModels"
import {ShowForumResult} from "../viewmodels/ForumViewModels"
import {Subscription} from "rxjs/Rx";

@Component({
    selector: "fnb-show-forum",
    templateUrl: "assets/frontend/show-forum.html"
})
export class ShowForumComponent implements OnInit, OnDestroy {

    constructor(private activatedRoute: ActivatedRoute,
                public settings: FnbSettings,
                private forumService: ForumService) {
    }

    ngOnInit() {
        this.idSubscription = this.activatedRoute.params.subscribe(params => {
            this.id = params['id']
            console.log("-- Create ShowForumComponent with id " + this.id)
            this.load()
        })
    }

    ngOnDestroy() {
        this.idSubscription.unsubscribe()
        console.log("-- Destroy ShowForumComponent with id " + this.id)
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
                if
                this.errorMessage = result.getError().error
                // this.failures.next(result.getError().error)
            }
        })
    }

    private id: string
    private idSubscription: Subscription

    // TODO: Error Handling
    public loaded: boolean = false
    public successful: boolean = true

    public errorMessage: string = ""
    public errorMessageLocalized: boolean = false
    public errorMessageParams: Object = {}

    public forum: ShowForumResult = null

    // TODO: Permissions
    public permissionCreateThread: boolean = true

}