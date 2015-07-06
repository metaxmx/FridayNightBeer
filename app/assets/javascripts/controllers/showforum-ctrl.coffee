
class ShowForumCtrl

    constructor: (@$log, @$scope, @$routeParams, @ForumsService) ->
        @$log.debug "constructing ShowForumCtrl"
        @getForum()

    getForum: () ->
        @$log.debug "getForum()"
        @ForumsService.loadForum(@$routeParams.id)
        .then(
            (data) =>
                @$log.debug "Promise returned Forum"
                @$scope.forum = data
            ,
            (error) =>
                @$log.error "Unable to get Forums: #{error}"
                @$scope.forum = {}
            )

controllersModule.controller('ShowForumCtrl', ShowForumCtrl)