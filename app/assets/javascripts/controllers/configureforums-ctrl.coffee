
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
        @$scope.categories = []
        @edits = {}
        @$scope.forumsStatus = new AjaxStatus (data) => @$scope.categories = data
        @$scope.categoryEdit = (category) => @getEditCategoryName(category)
#        @$scope.startEdit = (entity, prop, event) => @startEdit(entity, prop, event)
#        @$scope.saveEdit = (entity, prop) => @saveEdit(entity, prop)
#        @$scope.cancelEdit = (entity) => @cancelEdit(entity)
        @ForumService.loadConfigureForums(@$scope.forumsStatus)

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