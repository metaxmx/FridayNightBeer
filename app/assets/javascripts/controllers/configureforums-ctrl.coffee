
class PropertyEdit

    constructor: (@obj, @prop, @onStart) ->
        @edited = false
        @value = null

    edit: ($event) ->
        @edited = true
        @value = @obj[@prop]
        @onStart?($event)

    save: () ->
        @edited = false
        @obj[@prop] = @value
        @value = null
        console.log(@obj)

    cancel: () ->
        @edited = false
        @obj[@prop] = @value
        @value = null
        console.log(@obj)


class ConfigureForumsCtrl

    constructor: (@$log, @$scope, @ForumService) ->
        @$log.debug "constructing ForumsCtrl"
        @$scope.categories = []
        @edits = {}
        @$scope.forumsStatus = new AjaxStatus
        @$scope.categoryEdit = (category) => @getEditCategoryName(category)
#        @$scope.startEdit = (entity, prop, event) => @startEdit(entity, prop, event)
#        @$scope.saveEdit = (entity, prop) => @saveEdit(entity, prop)
#        @$scope.cancelEdit = (entity) => @cancelEdit(entity)
        @getAllForums()

    getAllForums: () ->
        @$log.debug "getAllForums()"
        @$scope.forumsStatus.load()
        @edits = {}
        @ForumService.loadConfigureForums()
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

    getEditCategoryName: (category) ->
        result = @edits['cat_name_' + category.id]
        if (!result?)
            result = new PropertyEdit(category, 'name')
            @edits['cat_name_' + category.id] = result
        console.log(@edits)
        result

#    startEdit: (entity, prop, event) ->
#        entity.edited=true
#        entity[prop + '-edit'] = entity[prop]
#        input = $(event.target).closest('.edit-container').find('input')
#        window.setTimeout () -> input.focus()

#    saveEdit: (entity, prop) ->
#        entity[prop] = entity[prop + '-edit']
#        entity.edited=false

#    cancelEdit: (entity) ->
#        entity.edited=false

controllersModule.controller('ConfigureForumsCtrl', ConfigureForumsCtrl)