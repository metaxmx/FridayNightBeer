import {Component} from "@angular/core"
import {FnbSettings} from "../../util/settings";

@Component({
    selector: "fnb-admin-forums",
    templateUrl: "forums.component.html"
})
export class ForumsComponent {

    constructor(public settings: FnbSettings) {

    }

}