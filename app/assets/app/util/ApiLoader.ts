import {ApiResult, ApiResponse} from "../viewmodels/GeneralViewModels"
import {BehaviorSubject} from "rxjs/BehaviorSubject"
import {Observable} from "rxjs/Observable"
import {Subject} from "rxjs/Subject"

export const STATUS_INITIAL = "initial"
export const STATUS_FAILURE = "failure"
export const STATUS_LOADING = "loading"
export const STATUS_SUCCESS = "success"

type ErrorHandler<A extends ApiResult> = (response: ApiResponse<A>) => string
type ApiSupplier<A extends ApiResult> = () => Observable<ApiResponse<A>>
type SuccessHandler<A extends ApiResult, T> = (apiResult: A) => T

function defaultErrorHandler<A extends ApiResult>(unsuccessfulResponse: ApiResponse<A>): string {
    return unsuccessfulResponse.getError().error
}

export interface ApiModelLoader<T> {
    status: Observable<string>
    failure: Observable<string>
    value: Observable<T>
    load():void
    clearError():void
    getApiModel(): ApiModelData<T>
}

export interface ApiModelData<T> {
    status: string
    failure: string
    value: T
}

class BoundApiModelData<T> {
    constructor(loader: ApiModelLoader<T>) {
        loader.failure.subscribe((msg: string) => this.failure = msg)
        loader.status.subscribe((status: string) => this.status = status)
        loader.value.subscribe((value: T) => this.value = value)
    }
    public status: string
    public failure: string
    public value: T
}

export class ApiLoader<A extends ApiResult, T> implements ApiModelLoader<T> {

    constructor(private apiSupplier: ApiSupplier<A>,
                private successHandler: SuccessHandler<A, T>,
                errorHandler?: ErrorHandler<A>) {
        this.errorHandler = errorHandler || defaultErrorHandler
    }

    private errorHandler: ErrorHandler<A>

    public status: BehaviorSubject<string> = new BehaviorSubject<string>(STATUS_INITIAL)

    public failure: Subject<string> = new Subject<string>()

    public value: Subject<T> = new Subject<T>()

    public load() {
        this.status.next(STATUS_LOADING)
        this.apiSupplier().subscribe((res: ApiResponse<A>) => {
            if (res.success) {
                const resolvedValue: T = this.successHandler(res.getResult())
                this.value.next(resolvedValue)
                this.status.next(STATUS_SUCCESS)
            } else {
                this.failure.next(this.errorHandler(res))
                this.status.next(STATUS_FAILURE)
            }
        })
    }

    public clearError() {
        this.status.next(STATUS_INITIAL)
        this.failure.next(null)
    }

    public getApiModel(): ApiModelData<T> {
        return new BoundApiModelData<T>(this)
    }

}