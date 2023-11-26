import {Component, OnInit, ViewChild} from '@angular/core';
import {DatasetService} from "../../../services/dataset.service";
import {Dataset} from "../../../models/Dataset";
import {HierarchicalService} from "../../../services/hierarchical.service";
import {ResourceService} from "../../../services/resource.service";
import {SnackbarService} from "../../../services/snackbar.service";
import {MatDialog} from "@angular/material/dialog";
import {ImageDialogComponent} from "./image-dialog/image-dialog.component";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";

@Component({
  selector: 'app-analysis',
  templateUrl: './analysis.component.html',
  styleUrl: './analysis.component.css'
})
export class AnalysisComponent implements OnInit {
  datasets: Dataset[] = [];
  selectedDataset: Dataset | null = null;
  selectedLinkage: string = 'single';
  numClusters: number = 0;
  sampleToggle: boolean = false;
  availableAttributes: any[] = [];
  selectedAttributes: any[] = [];


  runPressed: boolean = false;
  loadingResults: boolean = true;
  loadingParams: boolean = true;

  dendrogram: string;
  pararrelCoordinates: string;
  clusterAssignments: any[]

  dataSource: MatTableDataSource<any>;
  displayedColumns: string[];
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private hierarchicalService: HierarchicalService,
              private datasetService: DatasetService,
              private resourceService: ResourceService,
              private customSnackbarService: SnackbarService, private dialog: MatDialog) {
  }

  openImageDialog(imageUrl: string): void {
    this.dialog.open(ImageDialogComponent, {
      data: {imageUrl},
      width: 'auto',
      height: 'auto',
    });
  }

  ngOnInit(): void {
    this.datasetService.getDatasetList().subscribe((datasets) => {
      this.datasets = datasets;
    });
  }

  onDatasetSelect(): void {
    if (this.selectedDataset) {
      const readDataset$ = this.datasetService.readDataset(this.selectedDataset);

      readDataset$.subscribe((response) => {
        console.log(response);
        this.availableAttributes = [...response.attributes];
        this.selectedAttributes = new Array(this.availableAttributes.length).fill(false);
      });
    }
  }

  runAnalysis(): void {
    this.loadingResults = true;
    this.loadingParams = true;
    this.runPressed = true;

    const requestData = {
      filename: this.selectedDataset?.dataset,
      access_type: this.selectedDataset?.access_type,
      linkage: this.selectedLinkage,
      n_clusters: this.numClusters.toString(),
      sample: this.sampleToggle.toString(),
      attributes: this.availableAttributes
        .filter((option, index) => this.selectedAttributes[index])
    };

    this.hierarchicalService.runAnalysis(requestData).subscribe({
        next: (result) => {
          console.log('Result:', result);
          this.pollForStatus(requestData);
        },
        error: (error) => {
          this.customSnackbarService.open('Something went wrong running the algorithm', 'Close', {});
          console.error('Error:', error);

        }
      }
    );
  }

  private pollForStatus(requestData: any): void {
    this.hierarchicalService.runAnalysis(requestData).subscribe((result) => {
      console.log('Result:', result);

      if (result.status === 'RUNNING') {
        setTimeout(() => this.pollForStatus(requestData), 1000); // Adjust the delay as needed
      } else if (result.status === 'FINISHED') {
        console.log('Operation finished successfully!');
        this.getAnalysisResults(result.id);
      } else {
        console.error('Unexpected status:', result.status);
      }
    });
  }

  private getAnalysisResults(id: number): void {
    const imageTypes = ['dendrogram', 'parallel_coordinates'];

    imageTypes.forEach((type) => {
      this.resourceService.getAnalysisPlotResults(id, type).subscribe({
        next: (imageData: any) => {
          const imageUrl = this.createImageUrl(imageData);
          if (type === 'dendrogram') {
            this.dendrogram = imageUrl;
          } else if (type === 'parallel_coordinates') {
            this.pararrelCoordinates = imageUrl;
          }
        },
        error: (error) => {
          console.error(`Error fetching ${type} image:`, error);
        },
      });
    });

    this.resourceService.getAnalysisAssignmentsResults(id).subscribe({
      next: (result) => {
        this.clusterAssignments = result;
        this.displayedColumns = Object.keys(this.clusterAssignments[0]);
        this.dataSource = new MatTableDataSource(this.clusterAssignments);
        this.dataSource.paginator = this.paginator;
        this.loadingResults = false;

      },
      error: (error) => {
        console.error(`Error fetching data:`, error);
      },
    });

  }

  private createImageUrl(imageData: any): string {
    const arrayBuffer = new Uint8Array(imageData).buffer;

    const imageUrl = URL.createObjectURL(new Blob([arrayBuffer], {type: 'image/png'}));

    console.log(imageData);
    console.log(imageUrl);
    return imageUrl;
  }

  convertJsonToCsv(jsonData: any[]): string {
    if (!jsonData || jsonData.length === 0) {
      return '';
    }
    const csvHeader = Object.keys(jsonData[0]).join(',');
    const csvRows = jsonData.map(item => Object.values(item).join(','));
    return `${csvHeader}\n${csvRows.join('\n')}`;
  }

  downloadCsv(): void {
    const csvData = this.convertJsonToCsv(this.clusterAssignments);
    const blob = new Blob([csvData], {type: 'text/csv'});
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `clusters_${this.selectedDataset.dataset}`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }

}
