
import {Injectable} from "angular2/core"
import {Http, Response, Headers, RequestOptions} from "angular2/http"
import {Observable} from "rxjs/Observable"
import {ApiResult, ApiError, JsonParseApiException, validateSuccessfulResult} from "../viewmodels/GeneralViewModels"
import "../app-rxjs-operations.ts"

function toJsonString(data: any): string {
    if (typeof data === "string" || data instanceof String) {
        return <string>data
    }
    return JSON.stringify(data)
}

function parseApiResult<T extends ApiResult>(resultObj: Object):T | ApiError {
    if (!resultObj.hasOwnProperty("success")) {
        return <ApiError> {
            success: false,
            error: "Result object is invalid"
        }
    }
    let apiResult = <ApiResult>resultObj
    if (apiResult.success) {
        return <T> apiResult
    }
    if (apiResult.hasOwnProperty("error")) {
        return <ApiError> apiResult
    }
    return <ApiError> {
        success: false,
        error: "Undefined error"
    }
}

function parseResponse<T extends ApiResult>(res: Response): T {
    let json: Object = {}
    try {
        json = res.json()
    } catch (e) {
        throw new JsonParseApiException(e, res.status)
    }
    let apiResult = parseApiResult<T>(json)
    return validateSuccessfulResult<T>(apiResult, res.status)
}

@Injectable()
export class HttpCommunicationService {

    constructor(private http: Http) {}

    private apiVersion = "1.0"

    private headers = new Headers({"Content-Type": "application/json"})

    private requestOptions = new RequestOptions({headers: this.headers})

    private compileApiUrl(apiUrl: string): string {
        return `/api/${this.apiVersion}/${apiUrl}`
    }

    GET<T extends ApiResult>(apiUrl: string): Observable<T> {
        let url = this.compileApiUrl(apiUrl)
        let resultObservable = this.http.get(url, this.requestOptions)
        return resultObservable.map((res: Response) => parseResponse<T>(res))
    }

    POST<T extends ApiResult>(apiUrl: string, postBody: any): Observable<T> {
        let url = this.compileApiUrl(apiUrl)
        let postJsonStr = toJsonString(postBody)
        let resultObservable = this.http.post(url, postJsonStr, this.requestOptions)
        return resultObservable.map((res: Response) => parseResponse<T>(res))
    }

    PUT<T extends ApiResult>(apiUrl: string, putBody: any): Observable<T> {
        let url = this.compileApiUrl(apiUrl)
        let putJsonStr = toJsonString(putBody)
        let resultObservable = this.http.put(url, putJsonStr, this.requestOptions)
        return resultObservable.map((res: Response) => parseResponse<T>(res))
    }

    DELETE<T extends ApiResult>(apiUrl: string): Observable<T> {
        let url = this.compileApiUrl(apiUrl)
        let resultObservable = this.http.delete(url, this.requestOptions)
        return resultObservable.map((res: Response) => parseResponse<T>(res))
    }

}