
class AjaxStatus

    constructor: (@onSucceed) ->
        @setStatus(false, false, false)
        @reset()

    setStatus: (@loading, @error, @successful) ->

    reset: ->
        @errorData = null
        @statusCode = null
        @headers = {}

    load: ->
        @reset()
        @setStatus(true, false, false)

    succeed: (data, @statusCode, @headers) ->
        @setStatus(false, false, true)
        @errorData = null
        @onSucceed?(data)

    fail: (@errorData, @statusCode, @headers) ->
        @setStatus(false, true, false)

@AjaxStatus = AjaxStatus