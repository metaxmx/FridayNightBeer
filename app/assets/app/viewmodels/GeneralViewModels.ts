/**
 * General ViewModels.
 */

export interface ApiResult {
    success:boolean
}

export interface ApiError extends ApiResult {
    error:string
}

export class ApiException implements ApiError {

    public success = false
    public error:string
    public statusCode:number

    constructor(msg:string, statusCode:number) {
        this.error = msg || "Unknown Error"
        this.statusCode = statusCode || 0
    }

    toString():string {
        let status = ""
        if (this.statusCode) status = ` (Status Code ${this.statusCode})`
        return `API Error: ${this.error}${status}`
    }

}

export class JsonParseApiException extends ApiException {

    constructor(e:Object, statusCode?:number) {
        let message:string
        if (e.hasOwnProperty("message")) {
            message = (<Error> e).message
        } else {
            message = "Unknown error"
        }
        super(`Error parsing JSON: ${message}`, statusCode)
    }

}

export class UnsuccessfulApiException extends ApiException {

    constructor(apiError:ApiError, statusCode:number) {
        super(apiError.error, statusCode)
    }

}

export function isSuccessfulResult<T extends ApiResult>(result:T | ApiError):result is T {
    return result.success === true
}

export function validateSuccessfulResult<T extends ApiResult>(result:T | ApiError, statusCode:number):T {
    if (isSuccessfulResult(result)) {
        return result
    } else {
        throw new UnsuccessfulApiException(result, statusCode)
    }
}