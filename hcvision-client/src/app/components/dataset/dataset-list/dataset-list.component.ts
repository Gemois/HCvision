import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {DatasetService} from "../../../services/dataset/dataset.service";
import {Dataset} from "../../../models/Dataset";

@Component({
  selector: 'app-dataset-list',
  templateUrl: './dataset-list.component.html',
  styleUrl: './dataset-list.component.css'
})
export class DatasetListComponent implements OnInit {
  @Output() itemSelected: EventEmitter<Dataset> = new EventEmitter<Dataset>();

  datasets: Dataset[] = [];

  constructor(private datasetService: DatasetService) {
  }

  ngOnInit() {
    this.loadFileList();
  }

  loadFileList() {
    this.datasetService.getDatasets().subscribe(
      (response: Dataset[]) => {
        this.datasets = response;
      },
      (error: any) => {
        console.error('Error fetching file list:', error);
      }
    );
  }

  handleDatasetItemClick(index: number, dataset: Dataset) {
    console.log(dataset);
    this.itemSelected.emit(dataset);
    this.datasets.forEach((dataset, i) => {
      dataset.isSelected = i === index;
    });
  }

  protected readonly Dataset = Dataset;
}
