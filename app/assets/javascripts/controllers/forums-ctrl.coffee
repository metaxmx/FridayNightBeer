
class ForumsCtrl

    constructor: (@$log, @$scope, @ForumsService) ->
        @$log.debug "constructing ForumsCtrl"
        @$scope.categories = []
        @getAllForums()

    getAllForums: () ->
        @$log.debug "getAllForums()"
        @ForumsService.loadForums()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Forums/Categories"
                @$scope.categories = data
            ,
            (error) =>
                @$log.error "Unable to get Forums: #{error}"
                @$scope.categories = []
            )

controllersModule.controller('ForumsCtrl', ForumsCtrl)