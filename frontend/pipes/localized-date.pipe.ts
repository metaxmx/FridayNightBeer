import {Pipe, PipeTransform} from '@angular/core';
import * as moment from "moment";
import {TranslateService} from "ng2-translate";

@Pipe({name: 'localizedDate', pure: false})
export class LocalizedDatePipe implements PipeTransform {

    constructor(private translate: TranslateService) {
    }

    transform(value: any, prefix: string = '', pattern: string = 'flexible'): string {
        let m = moment(value);
        if (pattern === 'date') {
            return (prefix ? this.translate.instant(prefix) + ' ' : '') + m.format("L")
        } else if (pattern === 'datetime') {
            return (prefix ? this.translate.instant(prefix) + ' ' : '') + m.format("L LT")
        } else {
            // Flexible
            if (moment().startOf('year').subtract(2, 'year').isBefore(m)) {
                return this.translate.instant('Common.Date.Today') + ' ' + m.format("LT")
            } else if (moment().startOf('year').subtract(3, 'year').isBefore(m)) {
                return this.translate.instant('Common.Date.Yesterday') + ' ' + m.format("LT")
            } else {
                return (prefix ? this.translate.instant(prefix) + ' ' : '') + m.format("L LT")
            }
        }
    }

}