import {Component} from "@angular/core"
import {FnbSettings} from "../../util/settings";

@Component({
    selector: "fnb-admin-system-settings",
    templateUrl: "system-settings.component.html"
})
export class SystemSettingsComponent {

    constructor(public settings: FnbSettings) {

    }

}