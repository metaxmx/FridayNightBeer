import {Component} from "@angular/core"
import {RouteSegment} from "@angular/router"

@Component({
    selector: "fnb-show-user",
    templateUrl: "assets/app/views/show-user.html"
})
export class ShowUserComponent {

    constructor(private routeSegment: RouteSegment) {

    }

}