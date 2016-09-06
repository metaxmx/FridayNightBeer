import {Routes} from '@angular/router'

import {ForumOverviewComponent} from "./views/forum-overview.component"
import {ShowForumComponent} from "./views/show-forum.component"
import {ShowThreadComponent} from "./views/show-thread.component"
import {CreateThreadComponent} from "./views/create-thread.component"
import {RegisterComponent} from "./views/register.component"
import {LoginComponent} from "./views/login.component"
import {SettingsComponent} from "./views/settings.component"
import {MediaComponent} from "./views/media.component"
import {EventsComponent} from "./views/events.component"
import {UsersComponent} from "./views/users.component"
import {ShowUserComponent} from "./views/show-user.component"
import {AdminComponent} from "./views/admin.component"
import {SearchForumComponent} from "./views/search-forum.component";

export const APP_ROUTES: Routes = [
    {   path: "",                      component: ForumOverviewComponent   },
    {   path: "forum/:id",             component: ShowForumComponent       },
    {   path: "thread/:id",            component: ShowThreadComponent      },
    {   path: "forum/:id/newthread",   component: CreateThreadComponent    },
    {   path: "register",              component: RegisterComponent        },
    {   path: "login",                 component: LoginComponent           },
    {   path: "settings",              component: SettingsComponent        },
    {   path: "media",                 component: MediaComponent           },
    {   path: "events",                component: EventsComponent          },
    {   path: "users",                 component: UsersComponent           },
    {   path: "user/:id",              component: ShowUserComponent        },
    {   path: "admin",                 component: AdminComponent           },
    {   path: "search/forum",          component: SearchForumComponent     }
];