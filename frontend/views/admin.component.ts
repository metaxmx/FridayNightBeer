import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings";

@Component({
    selector: "fnb-admin",
    templateUrl: "assets/frontend/admin.html"
})
export class AdminComponent {

    constructor(public settings: FnbSettings) {

    }

}