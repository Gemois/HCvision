import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {DatasetService} from "../../../services/dataset.service";
import {Dataset} from "../../../models/Dataset";
import {HierarchicalService} from "../../../services/hierarchical.service";
import {ResourceService} from "../../../services/resource.service";
import {SnackbarService} from "../../../services/snackbar.service";
import {MatDialog} from "@angular/material/dialog";
import {ImageDialogComponent} from "./image-dialog/image-dialog.component";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {forkJoin, of, switchMap} from "rxjs";
import {NavigationEnd, Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-analysis',
  templateUrl: './analysis.component.html',
  styleUrl: './analysis.component.css'
})
export class AnalysisComponent implements OnInit {
  @Input() param_script: string;
  @Input() param_dataset: string;
  @Input() param_accessType: string;
  @Input() param_linkage: string;
  @Input() param_numClusters: number;
  @Input() param_attributes: string;
  @Input() param_sample: boolean;

  datasets: Dataset[] = [];
  selectedDataset: Dataset | null = null;
  selectedLinkage: string = 'single';
  numClusters: number = 2;
  sampleToggle: boolean = true;
  availableAttributes: any[] = [];
  selectedAttributes: any[] = [];

  script: string = null;
  runPressed: boolean = false;
  loadingResults: boolean = true;
  loadingParams: boolean = true;

  dendrogram: string;
  parallelCoordinates: string;
  clusterAssignments: any[]

  error: boolean = false;
  @Input() currentTab: number;


  dataSource: MatTableDataSource<any>;
  displayedColumns: string[];
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private hierarchicalService: HierarchicalService,
              private datasetService: DatasetService,
              private resourceService: ResourceService,
              private customSnackbarService: SnackbarService,private fb: FormBuilder, private dialog: MatDialog, private router:Router) {

  }

  openImageDialog(imageUrl: string): void {
    this.dialog.open(ImageDialogComponent, {
      data: {imageUrl},
      width: 'auto',
      height: 'auto',
    });
  }


  ngOnInit(): void {


    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {

        this.historyPreview();
      }
    });




    console.log(this.script);
    console.log(this.selectedLinkage);
    console.log(this.numClusters);
    if (!(this.param_script === 'Analysis')) {
      this.datasetService.getDatasetList().subscribe((datasets) => {
        this.datasets = datasets;
        this.runPressed = false;
        this.script = this.param_script;

      });
    } else {
      this.historyPreview();
    }
  }


  historyPreview() {
    const getDatasetList$ = this.datasetService.getDatasetList();

    forkJoin([getDatasetList$]).subscribe(([datasets]) => {
      this.datasets = datasets;
      this.selectedDataset = this.datasets.find(dataset =>
        dataset.dataset === this.param_dataset && dataset.access_type === this.param_accessType
      );
      this.numClusters = this.param_numClusters;
      this.selectedLinkage = this.param_linkage;
      this.sampleToggle = this.param_sample;

      const readDataset$ = this.datasetService.readDataset(this.selectedDataset);

      readDataset$.pipe(
        switchMap((response) => {
          this.availableAttributes = [...response.attributes];
          this.selectedAttributes = new Array(this.availableAttributes.length).fill(true);

          return of(response);
        })
      ).subscribe((response) => {
        this.param_attributes.split(" ").forEach(attribute => {
          const index = this.availableAttributes.indexOf(attribute);
          console.log(index)
          if (index !== -1) {
            this.selectedAttributes[index] = true;
            console.log(this.availableAttributes);
          }
        });
        this.runAnalysis();
      });
    });
  }


  onDatasetSelect(): void {
    if (this.selectedDataset) {
      const readDataset$ = this.datasetService.readDataset(this.selectedDataset);

      readDataset$.subscribe((response) => {
        this.availableAttributes = [...response.attributes];
        this.selectedAttributes = new Array(this.availableAttributes.length).fill(true);
      });
    }
  }

  runAnalysis(): void {

    // if (this.currentTab !== 1)
    //   return;

    // if (!this.atLeastOneAttribute())
    //   return;

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

  atLeastOneAttribute(): boolean {
    return this.selectedAttributes.some(attribute => attribute);
  }

  private pollForStatus(requestData: any): void {
    this.hierarchicalService.runAnalysis(requestData).subscribe((result) => {
      console.log('Result:', result);

      if (result.status === 'RUNNING') {
        setTimeout(() => this.pollForStatus(requestData), 1000); // Adjust the delay as needed
      } else if (result.status === 'FINISHED') {
        console.log('Operation finished successfully!');
        this.getAnalysisResults(result.id);
      } else if (result.status === 'ERROR'){
        this.error = true;
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
            this.parallelCoordinates = imageUrl;
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
