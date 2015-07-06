
class ShowForumCtrl

    constructor: (@$log, @$scope, @ForumsService) ->
        @$log.debug "constructing ShowForumCtrl"
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

controllersModule.controller('ShowForumCtrl', ShowForumCtrl)