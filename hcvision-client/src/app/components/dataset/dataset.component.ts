import {Component} from '@angular/core';
import {Dataset} from "../../models/Dataset";
import {UploadDialogComponent} from "./upload-dialog/upload-dialog.component";
import {DatasetService} from "../../services/dataset/dataset.service";
import {MatDialog} from "@angular/material/dialog";
import {CustomSnackbarService} from "../../services/custom-snackbar.service";

@Component({
  selector: 'app-dataset',
  templateUrl: './dataset.component.html',
  styleUrl: './dataset.component.css'
})
export class DatasetComponent {
  jsonData: any[] = [];
  selectedDataset: Dataset;

  constructor(private dialog: MatDialog,
              private datasetService: DatasetService,
              private customSnackbarService: CustomSnackbarService) {
  }

  previewDataset(dataset: Dataset) {
    if (dataset) {
      this.selectedDataset = dataset;
      this.datasetService.readDataset(dataset).subscribe({
          next: (response) => {
            this.jsonData = response.dataset;
            console.error('fetched JSON data successfully');
          },
          error: (error) => {
            console.error('Error fetching JSON data:', error);
          }
        }
      );
    }
  }

  uploadDataset() {
    const dialogRef = this.dialog.open(UploadDialogComponent, {
      width: '400px',
      data: {accessType: 'PRIVATE'}
    });

    dialogRef.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          this.datasetService.uploadDataset(result.file, result.accessType).subscribe({
            next: (response) => {
              this.customSnackbarService.open('File uploaded successfully', 'Close', {});
              console.error('File uploaded successfully');
              window.location.reload();
            },
            error: (error) => {
              console.error('Error uploading file:', error);
              this.customSnackbarService.open('Error uploading file', 'Close', {});
            }
          });
        }
      },
      error: (dialogError) => {
        console.error('Error with dialog:', dialogError);
      }
    });
  }

  downloadDataset() {
    if (this.selectedDataset) {
      this.datasetService.downloadDataset(this.selectedDataset).subscribe(
        (data: Blob) => {
          const downloadLink = document.createElement('a');
          const blob = new Blob([data], {type: 'application/octet-stream'});
          const url = window.URL.createObjectURL(blob);

          downloadLink.href = url;
          downloadLink.download = this.selectedDataset.dataset;
          document.body.appendChild(downloadLink);
          downloadLink.click();
          document.body.removeChild(downloadLink);
          window.URL.revokeObjectURL(url);

          console.error('File downloaded successfully');
          this.customSnackbarService.open('File downloaded successfully', 'Close', {});
        },
        (error) => {
          console.error('Error downloading dataset:', error);
          this.customSnackbarService.open('Error downloading dataset', 'Close', {});
        }
      );
    }
  }

  deleteDataset() {
    if (this.selectedDataset) {
      this.datasetService.deleteDataset(this.selectedDataset).subscribe({
          next: () => {
            window.location.reload();
            this.customSnackbarService.open('Dataset deleted successfully', 'Close', {});
            console.error('Dataset deleted successfully');
          },
          error: (error) => {
            this.customSnackbarService.open('Dataset deleted successfully', 'Close', {});
            console.error('Error deleting dataset:', error);
          }
        }
      );
    }
  }

}
