import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings";

@Component({
    selector: "fnb-admin",
    templateUrl: "admin.component.html"
})
export class AdminComponent {

    constructor(public settings: FnbSettings) {

    }

}