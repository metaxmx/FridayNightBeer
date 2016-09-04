/**
 * General ViewModels.
 */

export interface ApiResult {
    success:boolean
}

export interface ApiError extends ApiResult {
    error:string
    errorLocalized?:boolean
    errorParams?:Object
}

export class LocalizedError {
    constructor(public errorKey: string, public errorParams?:Object) {
    }
    toApiError(): ApiError {
        return new ApiErrorFromLocalizedMessage(this.errorKey, this.errorParams)
    }
}

export class ApiErrorFromMessage implements ApiError {
    constructor(public error: string) {}
    success = false
}

export class ApiErrorFromLocalizedMessage implements ApiError {
    constructor(public error: string, public errorParams?: Object) {}
    success = false
    errorLocalized = true
}

export function createApiErrorResponse<T extends ApiResult>(statusCode: number, err: any): ApiResponse<T> {
    let apiError: ApiError
    if (err instanceof LocalizedError) {
        apiError = err.toApiError()
    } else if (err instanceof Error) {
        apiError = new ApiErrorFromMessage(err.toString())
    } else if (typeof err === "string" || err instanceof String) {
        apiError = new ApiErrorFromMessage(err)
    } else {
        apiError = new ApiErrorFromLocalizedMessage("CommonErrors.Unknown")
    }
    return new ApiResponseFromError<T>(statusCode, apiError)
}

export interface ApiResponse<T extends ApiResult> {
    success: boolean
    statusCode:number
    getResult(): T
    getError(): ApiError
}

export class ApiResponseFromError<T extends ApiResult> implements ApiResponse<T> {
    constructor(public statusCode: number, private error: ApiError) { }
    success = false
    getResult(): T {
        throw new Error("Called getResult() on Error response")
    }
    getError(): ApiError {
        return this.error
    }
    toString(): string {
        return `API Error: ${this.error.error} (Status Code ${this.statusCode})`
    }
}

export class ApiResponseFromResult<T extends ApiResult> implements ApiResponse<T> {
    constructor(public statusCode: number, private result: T) { }
    success = true
    getResult(): T {
        return this.result
    }
    getError(): ApiError {
        throw new Error("Called getError() on Result response")
    }
    toString(): string {
        return `API Result (Status Code ${this.statusCode})`
    }
}