import {Component, OnInit, OnDestroy} from "@angular/core"
import {ActivatedRoute} from "@angular/router"
import {Subscription} from "rxjs/Rx"

@Component({
    selector: "fnb-show-user",
    templateUrl: "assets/app/views/show-user.html"
})
export class ShowUserComponent implements OnInit, OnDestroy {

    constructor(private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        this.idSubscription = this.activatedRoute.params.subscribe(params => {
            this.id = params['id']
            console.log("-- Create ShowUserComponent with id " + this.id)
            this.load()
        })
    }

    ngOnDestroy() {
        this.idSubscription.unsubscribe()
        console.log("-- Destroy ShowUserComponent with id " + this.id)
    }

    load() {
        // TODO
    }

    public id: string
    private idSubscription: Subscription

}