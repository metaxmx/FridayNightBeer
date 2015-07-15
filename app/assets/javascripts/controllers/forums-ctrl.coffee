
class ForumsCtrl

    constructor: (@$log, @$scope, @ForumService) ->
        @$log.debug "constructing ForumsCtrl"
        @$scope.categories = []
        @$scope.forumsStatus = new AjaxStatus
        @getAllForums()

    getAllForums: () ->
        @$log.debug "getAllForums()"
        @$scope.forumsStatus.load()
        @ForumService.loadForums()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Forums/Categories"
                @$scope.categories = data
                @$scope.forumsStatus.succeed()
            ,
            (error) =>
                @$log.error "Unable to get Forums: #{error}"
                @$scope.categories = []
                @$scope.forumsStatus.fail(error)
            )

controllersModule.controller('ForumsCtrl', ForumsCtrl)