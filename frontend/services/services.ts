import {AuthenticationService} from "./authentication.service"
import {HttpCommunicationService} from "./http-communication.service"
import {ForumService} from "./forum.service"
import {ThreadService} from "./thread.service"
import {LanguageService} from "./language.service"

export const FNB_SERVICE_PROVIDERS = [
    LanguageService,
    AuthenticationService,
    HttpCommunicationService,
    ForumService,
    ThreadService
];