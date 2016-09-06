import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings";

@Component({
    selector: "fnb-work-in-progress",
    templateUrl: "assets/frontend/widgets/work-in-progress.html"
})
export class WorkInProgressComponent {

    constructor(public settings: FnbSettings) {

    }

}