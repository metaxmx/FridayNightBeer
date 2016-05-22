import {Component} from "angular2/core"
import {ROUTER_DIRECTIVES, RouteParams} from "angular2/router"

@Component({
    selector: "fnb-show-forum",
    templateUrl: "assets/app/components/show-forum.html",
    directives: [ROUTER_DIRECTIVES]
})
export class ShowForumComponent {

    constructor(private routeParams: RouteParams) {
        console.log("-- Create ShowForumComponent with id " + routeParams.get("id"))
        this.id = routeParams.get("id")
    }

    private id: string

}