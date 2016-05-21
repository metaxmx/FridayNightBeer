import {bootstrap} from "angular2/platform/browser"
import TodoAppComponent from "./app"
import {LocalStorageTodoStore} from "./services/store"
import {TodoStore} from "./services/todo.store"
import {provide} from "angular2/core"
import {HTTP_PROVIDERS} from "angular2/http"

/**
 * Bootstrap application.
 */
bootstrap(TodoAppComponent, [
    HTTP_PROVIDERS,
    provide(TodoStore, {useClass: LocalStorageTodoStore})
])
