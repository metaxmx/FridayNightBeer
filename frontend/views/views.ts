import {AdminComponent} from "./admin.component";
import {CreateThreadComponent} from "./create-thread.component";
import {EventsComponent} from "./events.component";
import {FooterComponent} from "./footer.component";
import {ForumOverviewComponent} from "./forum-overview.component";
import {HeaderComponent} from "./header.component";
import {LoginComponent} from "./login.component";
import {MediaComponent} from "./media.component";
import {RegisterComponent} from "./register.component";
import {SettingsComponent} from "./settings.component";
import {ShowForumComponent} from "./show-forum.component";
import {ShowThreadComponent} from "./show-thread.component";
import {ShowUserComponent} from "./show-user.component";
import {UsersComponent} from "./users.component";
import {SearchForumComponent} from "./search-forum.component";
import {SystemSettingsComponent} from "./admin/system-settings.component";

export const FNB_ADMIN_VIEWS: any[] = [
    SystemSettingsComponent
];

export const FNB_VIEWS: any[] = [
    AdminComponent,
    CreateThreadComponent,
    EventsComponent,
    FooterComponent,
    ForumOverviewComponent,
    HeaderComponent,
    LoginComponent,
    MediaComponent,
    RegisterComponent,
    SettingsComponent,
    ShowForumComponent,
    ShowThreadComponent,
    ShowUserComponent,
    UsersComponent,
    SearchForumComponent,
    FNB_ADMIN_VIEWS
];