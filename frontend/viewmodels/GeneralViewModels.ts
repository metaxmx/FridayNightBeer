/**
 * General ViewModels.
 */

export interface ApiResult {
    success:boolean
}

export interface ApiError extends ApiResult {
    error:string
}

class ApiErrorFromMessage implements ApiError {
    constructor(public error: string) {}
    success = false
}

export function createApiErrorResponse<T extends ApiResult>(statusCode: number, err: any): ApiResponse<T> {
    let errorMsg: string = "Unknown error"
    if (err instanceof Error) {
        errorMsg = err.toString()
    } else if (typeof err === "string" || err instanceof String) {
        errorMsg = err
    }
    return new ApiResponseFromError<T>(statusCode, new ApiErrorFromMessage(errorMsg))
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