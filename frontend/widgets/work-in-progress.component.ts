import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings";

@Component({
    selector: "fnb-work-in-progress",
    templateUrl: "work-in-progress.component.html"
})
export class WorkInProgressComponent {

    constructor(public settings: FnbSettings) {

    }

}