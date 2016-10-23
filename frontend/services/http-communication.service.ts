import {Injectable} from "@angular/core"
import {Http, Response, Headers, RequestOptions} from "@angular/http"
import {Observable} from "rxjs/Observable"
import {
    ApiResult, ApiError, createApiErrorResponse, ApiResponse,
    ApiResponseFromError, ApiResponseFromResult, LocalizedError
} from "../viewmodels/GeneralViewModels"
import "../app-rxjs-operations"
import {FnbEnvironment} from "../util/settings";

function toJsonString(data: any): string {
    if (typeof data === "string" || data instanceof String) {
        return <string>data
    }
    return JSON.stringify(data)
}

function parseResponse<T extends ApiResult>(res: Response): ApiResponse<T> {
    try {
        let json = res.json();
        if (!json.hasOwnProperty("success"))
            return createApiErrorResponse<T>(res.status, new LocalizedError("CommonErrors.InvalidJson"));
        let apiResult = <ApiResult> json;
        if (!apiResult.success) {
            if (apiResult.hasOwnProperty("error"))
                return new ApiResponseFromError<T>(res.status, <ApiError> apiResult);
            return createApiErrorResponse<T>(res.status, new LocalizedError("CommonErrors.InvalidJson"))
        }
        return new ApiResponseFromResult<T>(res.status, <T> apiResult)
    } catch (e) {
        return createApiErrorResponse<T>(res.status, e)
    }
}

@Injectable()
export class HttpCommunicationService {

    constructor(private env: FnbEnvironment,
                private http: Http) {
        console.debug("Initialize Service HttpCommunicationService")
        console.debug("API Root is " + env.apiRoot)
    }

    private apiVersion = "1.0";

    private headers = new Headers({"Content-Type": "application/json"});

    private requestOptions = new RequestOptions({headers: this.headers});

    private getUrl(apiUrl: string): string {
        return `${this.env.apiRoot}/${this.apiVersion}/${apiUrl}`
    }

    private static handleResponse<T extends ApiResult>(responseObservable: Observable<Response>): Observable<ApiResponse<T>> {
        return responseObservable.catch(
            (errRes: any) => Observable.of(<Response>errRes)
        ).map(
            (res: Response) => parseResponse<T>(res)
        )
    }

    GET<T extends ApiResult>(apiUrl: string): Observable<ApiResponse<T>> {
        let responseObservable = this.http.get(this.getUrl(apiUrl));
        return HttpCommunicationService.handleResponse<T>(responseObservable)
    }

    HEAD<T extends ApiResult>(apiUrl: string): Observable<ApiResponse<T>> {
        let responseObservable = this.http.head(this.getUrl(apiUrl));
        return HttpCommunicationService.handleResponse<T>(responseObservable)
    }

    POST<T extends ApiResult>(apiUrl: string, postBody: any): Observable<ApiResponse<T>> {
        let responseObservable = this.http.post(this.getUrl(apiUrl), toJsonString(postBody), this.requestOptions);
        return HttpCommunicationService.handleResponse<T>(responseObservable)
    }

    PUT<T extends ApiResult>(apiUrl: string, putBody: any): Observable<ApiResponse<T>> {
        let responseObservable = this.http.put(this.getUrl(apiUrl), toJsonString(putBody), this.requestOptions);
        return HttpCommunicationService.handleResponse<T>(responseObservable)
    }

    DELETE<T extends ApiResult>(apiUrl: string): Observable<ApiResponse<T>> {
        let responseObservable = this.http.delete(this.getUrl(apiUrl));
        return HttpCommunicationService.handleResponse<T>(responseObservable)
    }

}