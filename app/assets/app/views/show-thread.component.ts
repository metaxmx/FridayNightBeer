import {Component} from "@angular/core"
import {ROUTER_DIRECTIVES, RouteSegment} from "@angular/router"
import {FnbSettings} from "../util/settings"
import {AlertComponent} from "ng2-bootstrap/components/alert"
import {ThreadService, ShowThreadData} from "../services/thread.service"
import {ApiModelLoader, ApiModelData} from "../util/ApiLoader"

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
        this.showThreadStatus = threadService.createShowThreadStatus(this.id)
        this.model = this.showThreadStatus.getApiModel()
        this.load()
    }

    private showThreadStatus: ApiModelLoader<ShowThreadData>

    public model: ApiModelData<ShowThreadData>

    load() {
        this.showThreadStatus.load()
    }

    showReply() {
        alert("Not Implemented yet")
    }

    public id: string

    // TODO: Permissions
    public permissionReply: boolean = true
    public permissionAvatar: boolean = true

}