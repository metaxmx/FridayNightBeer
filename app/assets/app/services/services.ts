import {AuthenticationService} from "./authentication.service"
import {HttpCommunicationService} from "./http-communication.service"
import {ForumService} from "./forum.service"

export const FNB_SERVICE_PROVIDES = [AuthenticationService, HttpCommunicationService, ForumService]