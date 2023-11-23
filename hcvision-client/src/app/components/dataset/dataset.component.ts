import {Component, Output} from '@angular/core';
import {Dataset} from "../../models/Dataset";
import {UploadDialogComponent} from "./upload-dialog/upload-dialog.component";
import {DatasetService} from "../../services/dataset/dataset.service";
import {MatDialog} from "@angular/material/dialog";
import {DatasetResponse} from "../../models/DatasetResponse";

@Component({
  selector: 'app-dataset',
  templateUrl: './dataset.component.html',
  styleUrl: './dataset.component.css'
})
export class DatasetComponent {
  @Output() jsonData: any[] = [];

  constructor(private dialog: MatDialog, private datasetService: DatasetService) {
  }


  previewDataset(dataset: Dataset) {
    if (dataset) {
      console.log(dataset)
      this.datasetService.getDataset(dataset).subscribe(
        (response: DatasetResponse) => {
          this.jsonData = response.dataset;
        },
        (error) => {
          console.error('Error fetching JSON data:', error);
        }
      );
    }
  }

  uploadFile() {
    const dialogRef = this.dialog.open(UploadDialogComponent, {
      width: '400px',
      data: {accessType: 'PRIVATE'}
    });

    dialogRef.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          this.datasetService.uploadFile(result.file, result.accessType).subscribe({
            next: (response) => {
              console.log('File uploaded successfully:', response);
              window.location.reload();

            },
            error: (error) => {
              console.error('Error uploading file:', error);
            }
          });
        }
      },
      error: (dialogError) => {
        console.error('Error with dialog:', dialogError);
      }
    });
  }

  downloadFile() {
  }

  deleteDataset() {
  }
}
