/**
 * General ViewModels.
 */

export interface ApiResult {
    success:boolean
}

export interface ApiError extends ApiResult {
    error:string
    statusCode?:number
}

export function asJson(result:any, onError:Object):Object {
    if (typeof result === "string" || result instanceof String) {
        try {
            return JSON.parse(result)
        } catch (e) {
            return onError
        }
    } else if (typeof result === "object") {
        return result
    }
    return onError
}

export function parseApiResult<T extends ApiResult>(result:any):T | ApiError {
    let resultObj = asJson(result, {success: false, error: "Cannot parse JSON"})
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