
import {Injectable} from "angular2/core";
import {Http} from "angular2/http";
import "rxjs/add/operator/toPromise";
import {parseApiResult} from "../viewmodels/GeneralViewModels"

@Injectable()
export class HttpCommunicationService {

    private apiVersion = "1.0"

    private compileApiUrl(apiUrl: string): string {
        return `/api/${this.apiVersion}/${apiUrl}`
    }

    constructor(private http: Http) {
    }

    getRequest<T>(apiUrl: string): Promise<T> {
        let url = this.compileApiUrl(apiUrl)
        return null // TODO: Mock
    }

    postRequest<T>(apiUrl: string): Promise<T> {
        let url = this.compileApiUrl(apiUrl)
        return null // TODO: Mock
    }

    putRequest<T>(apiUrl: string): Promise<T> {
        let url = this.compileApiUrl(apiUrl)
        return null // TODO: Mock
    }

    deleteRequest<T>(apiUrl: string): Promise<T> {
        let url = this.compileApiUrl(apiUrl)
        return null // TODO: Mock
    }

}