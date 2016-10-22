import {Component} from "@angular/core"
import {FnbSettings} from "../../util/settings";

@Component({
    selector: "fnb-admin-forums",
    templateUrl: "assets/frontend/admin/forums.html"
})
export class ForumsComponent {

    constructor(public settings: FnbSettings) {

    }

}