
class AjaxStatus

    constructor: ->
        @succeed()

    load: ->
        @loading = true
        @error = false
        @successful = false
        @errormessage = null

    succeed: ->
        @loading = false
        @error = false
        @successful = true
        @errormessage = null

    fail: (msg) ->
        @loading = false
        @error = true
        @successful = false
        @errormessage = msg

@AjaxStatus = AjaxStatus