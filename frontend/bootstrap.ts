import {platformBrowserDynamic} from '@angular/platform-browser-dynamic'
import { enableProdMode } from '@angular/core'
import {AppModule} from './app.module'

/**
 * Bootstrap application
 */

console.info("Bootstrapping Friday Night Beer ...");
// enableProdMode();
platformBrowserDynamic().bootstrapModule(AppModule)
    .then(success => console.info("Friday Night Beer loaded successfully"))
    .catch(err => console.error(err));
