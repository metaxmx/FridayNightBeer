import {AuthenticationService} from "./authentication.service"
import {Injectable} from "angular2/core"

@Injectable()
export class ForumService {

    constructor(private authService:AuthenticationService) {
        console.debug("Initialize Service ForumService")
    }

}