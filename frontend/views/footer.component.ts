import {Component} from "@angular/core"
import {FnbSettings} from "../util/settings"

@Component({
    selector: "fnb-footer",
    templateUrl: "assets/frontend/footer.html"
})
export class FooterComponent {

    constructor(public settings: FnbSettings) { }

}