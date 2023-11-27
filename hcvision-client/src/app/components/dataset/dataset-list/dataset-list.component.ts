import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DatasetService} from "../../../services/dataset.service";
import {Dataset} from "../../../models/Dataset";

@Component({
  selector: 'app-dataset-list',
  templateUrl: './dataset-list.component.html',
  styleUrl: './dataset-list.component.css'
})
export class DatasetListComponent {
  @Output() itemSelected: EventEmitter<Dataset> = new EventEmitter<Dataset>();

  @Input() datasets: Dataset[] = [];

  constructor(private datasetService: DatasetService) {
  }

  onDatasetSelect(index: number, dataset: Dataset) {
    console.log(dataset);
    this.itemSelected.emit(dataset);
    this.datasets.forEach((dataset, i) => {
      dataset.isSelected = i === index;
    });
  }

  protected readonly Dataset = Dataset;
}
