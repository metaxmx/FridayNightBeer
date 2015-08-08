
class ForumsCtrl

    constructor: (@$log, @$scope, @ForumService) ->
        @$scope.categories = []
        @$scope.forumsStatus = new AjaxStatus (data) => @$scope.categories = data
        @ForumService.loadForums(@$scope.forumsStatus)

controllersModule.controller('ForumsCtrl', ForumsCtrl)