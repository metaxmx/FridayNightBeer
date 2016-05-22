import {Component, Input} from "angular2/core"
import {FnbSettings} from "../util/settings"

@Component({
    selector: "fnb-forum-overview",
    templateUrl: "assets/app/components/forum-overview.html"
})
export class ForumOverviewComponent {

    constructor(public settings: FnbSettings) {
    }

    @Input() label: string

}