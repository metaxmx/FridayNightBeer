import {platformBrowserDynamic} from '@angular/platform-browser-dynamic'
import {AppModule} from './app.module'

/**
 * Bootstrap application.
 */
console.info("Bootstrapping Friday Night Beer");
platformBrowserDynamic().bootstrapModule(AppModule).catch(err => console.error(err));
