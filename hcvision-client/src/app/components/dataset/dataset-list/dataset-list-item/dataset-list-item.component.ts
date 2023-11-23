import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-dataset-list-item',
  templateUrl: './dataset-list-item.component.html',
  styleUrl: './dataset-list-item.component.css'
})
export class DatasetListItemComponent {
  @Input() datasetName: string = '';
  @Input() accessType: string = '';
  @Input() isSelected: boolean = false;  // Input to determine selection state
  @Output() itemClick: EventEmitter<void> = new EventEmitter<void>();

  handleItemClick() {
    if (!this.isSelected) {
      this.itemClick.emit();
    }
  }

}
