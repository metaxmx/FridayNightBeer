import {Component, Input} from "angular2/core"
import {FnbSettings} from "../util/settings"
import {ROUTER_DIRECTIVES} from "angular2/router"

@Component({
    selector: "fnb-forum-overview",
    templateUrl: "assets/app/components/forum-overview.html",
    directives: [ROUTER_DIRECTIVES]
})
export class ForumOverviewComponent {

    constructor(public settings: FnbSettings) {
    }

}