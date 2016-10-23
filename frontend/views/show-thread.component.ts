import {Component, OnInit, OnDestroy} from "@angular/core"
import {ActivatedRoute} from "@angular/router"
import {FnbSettings} from "../util/settings"
import {ThreadService, ShowThreadData} from "../services/thread.service"
import {ApiModelLoader, ApiModelData} from "../util/ApiLoader"
import {Subscription} from "rxjs/Rx";

@Component({
    selector: "fnb-show-thread",
    templateUrl: "show-thread.component.html"
})
export class ShowThreadComponent implements  OnInit, OnDestroy {

    constructor(private activatedRoute: ActivatedRoute,
                public settings: FnbSettings,
                private threadService: ThreadService) {
    }

    ngOnInit() {
        this.idSubscription = this.activatedRoute.params.subscribe(params => {
            this.id = params['id']
            console.log("-- Create ShowThreadComponent with id " + this.id)
            this.load()
        })
    }

    ngOnDestroy() {
        this.idSubscription.unsubscribe()
        console.log("-- Destroy ShowThreadComponent with id " + this.id)
    }

    private showThreadStatus: ApiModelLoader<ShowThreadData>
    public model: ApiModelData<ShowThreadData>

    load() {
        this.showThreadStatus = this.threadService.createShowThreadStatus(this.id)
        this.model = this.showThreadStatus.getApiModel()
        this.showThreadStatus.load()
    }

    showReply() {
        alert("Not Implemented yet")
    }

    public id: string
    private idSubscription: Subscription

    // TODO: Permissions
    public permissionReply: boolean = true
    public permissionAvatar: boolean = true

}