import {Component} from "angular2/core"
import {FnbSettings} from "../util/settings"

@Component({
    selector: "fnb-footer",
    templateUrl: "assets/app/views/footer.html"
})
export class FooterComponent {

    constructor(public settings: FnbSettings) { }

}