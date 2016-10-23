import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings"

@Component({
    selector: "fnb-footer",
    templateUrl: "footer.component.html"
})
export class FooterComponent {

    constructor(public settings: FnbSettings) { }

}