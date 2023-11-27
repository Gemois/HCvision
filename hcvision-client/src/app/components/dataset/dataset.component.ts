import {Component, OnInit} from '@angular/core';
import {Dataset} from "../../models/Dataset";
import {UploadDialogComponent} from "./upload-dialog/upload-dialog.component";
import {DatasetService} from "../../services/dataset.service";
import {MatDialog} from "@angular/material/dialog";
import {SnackbarService} from "../../services/snackbar.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-dataset',
  templateUrl: './dataset.component.html',
  styleUrl: './dataset.component.css'
})
export class DatasetComponent implements OnInit {
  jsonData: any[] = [];
  selectedDataset: Dataset;
  datasets: Dataset[];
  datasetPreviewLoading: boolean = false;

  constructor(private dialog: MatDialog,
              private datasetService: DatasetService,
              private customSnackbarService: SnackbarService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.showDatasetList();
  }

  showDatasetList() {
    this.datasetService.getDatasetList().subscribe({
        next: (response) => {
          this.datasets = response;
        },
        error: (error: any) => {
          console.error('Error fetching file list:', error);
        }
      }
    );
  }


  previewDataset(dataset: Dataset) {
    this.datasetPreviewLoading = true;
    if (dataset) {
      this.selectedDataset = dataset;
      this.datasetService.readDataset(dataset).subscribe({
          next: (response) => {
            this.datasetPreviewLoading = false;
            this.jsonData = response.dataset;
            console.log('fetched JSON data successfully');
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
              this.reloadDatasetList();
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
            this.reloadDatasetList();
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

  reloadDatasetList() {
    this.showDatasetList();
  }


}
