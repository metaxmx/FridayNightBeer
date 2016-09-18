import {Component} from "@angular/core"
import {FnbSettings} from "../../util/settings";

@Component({
    selector: "fnb-admin-system-settings",
    templateUrl: "assets/frontend/admin/system-settings.html"
})
export class SystemSettingsComponent {

    constructor(public settings: FnbSettings) {

    }

}