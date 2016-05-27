import {Injectable} from "@angular/core"
import {HttpCommunicationService} from "./http-communication.service"
import {ApiResponse} from "../viewmodels/GeneralViewModels"
import {Observable} from "rxjs/Observable"
import {ShowThreadResult} from "../viewmodels/ThreadsViewModels"

const showThreadApiUrl = "thread/"

@Injectable()
export class ThreadService {

    constructor(private httpService: HttpCommunicationService) {
        console.debug("Initialize Service ThreadService")
    }

    public loadThread(id: string): Observable<ApiResponse<ShowThreadResult>> {
        console.log("--- Send Show Thread Request (id = " + id + ")")
        return this.httpService.GET<ShowThreadResult>(showThreadApiUrl + id)
    }

}