import {AuthenticationService} from "./authentication.service"
import {Injectable} from "@angular/core"
import {HttpCommunicationService} from "./http-communication.service"
import {ForumOverviewCategory, ForumOverviewResult, ShowForumResult} from "../viewmodels/ForumViewModels"
import {ApiResponse} from "../viewmodels/GeneralViewModels"
import {BehaviorSubject} from "rxjs/BehaviorSubject"
import {Observable} from "rxjs/Observable"

export interface OverviewForum {
    id:string
    name:string
    description:string
    numThreads:number
    numPosts:number
    hasLastPost:boolean
    lastPostId:string
    lastPostTitle:string
    lastPostUser:string
    lastPostUserName:string
    lastPostDate:Date
}

export interface OverviewForumCategory {
    id: string
    name: string
    forums: Array<OverviewForum>
}

class OverviewForumData implements  OverviewForum {
    constructor(public id: string,
                public name: string,
                public description:string,
                public numThreads:number,
                public numPosts:number,
                public hasLastPost:boolean,
                public lastPostId:string,
                public lastPostTitle:string,
                public lastPostUser:string,
                public lastPostUserName:string,
                public lastPostDate:Date) {
    }
}

class OverviewForumCategoryData implements OverviewForumCategory {
    constructor(public id: string,
                public name: string,
                public forums: Array<OverviewForum>) {
    }
}

function fromOverviewViewModel(categoryViewModels: Array<ForumOverviewCategory>): Array<OverviewForumCategory> {
    let result: Array<OverviewForumCategory> = []
    for (let cat of categoryViewModels) {
        let forums: Array<OverviewForum> = []
        for (let f of cat.forums) {
            let forum: OverviewForumData
            if (f.lastPost) {
                let lp = f.lastPost
                forum = new OverviewForumData(f.id, f.name, f.description || "", f.numThreads, f.numPosts, true,
                    lp.id, lp.title, lp.user, lp.userName, new Date(lp.date))
            } else {
                forum = new OverviewForumData(f.id, f.name, f.description || "", f.numThreads, f.numPosts, false,
                    null, null, null, null, null)
            }
            forums.push(forum)
        }
        let category = new OverviewForumCategoryData(cat.id, cat.name, forums)
        result.push(category)
    }
    return result
}

const forumOverviewApiUrl = "forums"

const showForumApiUrl = "forum/"

@Injectable()
export class ForumService {

    constructor(private authService:AuthenticationService,
                private httpService: HttpCommunicationService) {
        console.debug("Initialize Service ForumService")
        this.refreshOverview()
    }

    public refreshOverview(): void {
        console.log("--- Send Forum Overview Request")
        this.httpService.GET<ForumOverviewResult>(forumOverviewApiUrl).subscribe((result: ApiResponse<ForumOverviewResult>) => {
            if (result.success) {
                this.forumOverviewData.next(fromOverviewViewModel(result.getResult().categories))
            } else {
                console.warn("Unsuccessful request: ", result.toString())
                // this.failures.next(result.getError().error)
            }
        })
    }

    public loadForum(id: string): Observable<ApiResponse<ShowForumResult>> {
        console.log("--- Send Show Forum Request (id = " + id + ")")
        return this.httpService.GET<ShowForumResult>(showForumApiUrl + id)
    }

    public forumOverviewData = new BehaviorSubject<Array<OverviewForumCategory>>([])

}