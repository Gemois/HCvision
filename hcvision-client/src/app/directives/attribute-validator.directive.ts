import {Directive, Input} from '@angular/core';
import {NG_VALIDATORS, AbstractControl, ValidationErrors, Validator} from '@angular/forms';

@Directive({
  selector: '[appAttributeValidator]',
  providers: [{provide: NG_VALIDATORS, useExisting: AttributeValidatorDirective, multi: true}]
})
export class AttributeValidatorDirective implements Validator {
  @Input('appAttributeValidator') selectedAttributes: boolean[];

  validate(control: AbstractControl): ValidationErrors | null {
    if (!this.selectedAttributes || this.selectedAttributes.every(attribute => !attribute)) {
      return {'atLeastOneAttribute': true};
    }
    return null;
  }
}
