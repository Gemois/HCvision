import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Dataset} from "../../../models/Dataset";
import {HierarchicalService} from "../../../services/hierarchical.service";
import {DatasetService} from "../../../services/dataset.service";
import {ResourceService} from "../../../services/resource.service";
import {SnackbarService} from "../../../services/snackbar.service";
import {forkJoin, of, switchMap} from "rxjs";
import {Router} from "@angular/router";
import {ChartDialogComponent} from "./chart-dialog/chart-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {OptimalResultsHelpDialogComponent} from "./optimal-help-dialog/optimal-results-help-dialog.component";
import {OptimalRunHelpDialogComponent} from "./optimal-run-help-dialog/optimal-run-help-dialog.component";

@Component({
  selector: 'app-optimal',
  templateUrl: './optimal.component.html',
  styleUrl: './optimal.component.css'
})
export class OptimalComponent implements OnInit {
  @Input() param_script: string;
  @Input() param_dataset: string;
  @Input() param_accessType: string;
  @Input() param_attributes: string;
  @Input() param_sample: boolean;
  @Input() currentTab: number;

  datasets: Dataset[] = [];
  selectedDataset: Dataset | null = null;
  sampleToggle: boolean = true;
  availableAttributes: any[] = [];
  selectedAttributes: boolean[];

  duration: number;


  chartData: any[] = [];
  chartLabels: string[] = [];
  chartOptions: any = {
    scales: {
      yAxes: [
        {
          id: 'y-axis-0',
          position: 'left',
          scaleLabel: {
            display: true,
            labelString: 'Clusters'
          },
          ticks: {
            beginAtZero: true,
            callback: function (value, index, values) {
              return value;
            }
          }
        },
        {
          id: 'y-axis-1',
          position: 'right',
          scaleLabel: {
            display: true,
            labelString: 'Max Inconsistency'
          },
          ticks: {
            beginAtZero: true,
            callback: function (value, index, values) {
              return value.toFixed(2);
            }
          }
        }
      ]
    }
  };

  recommendedLinkage: string | null = null;
  recommendedNumClusters: number | null = null;

  runPressed: boolean = false;
  loadingResults: boolean = true;
  loadingParams: boolean = true;
  error: boolean = false;

  constructor(private hierarchicalService: HierarchicalService,
              private datasetService: DatasetService,
              private resourceService: ResourceService,
              private customSnackbarService: SnackbarService, private router: Router, private dialog: MatDialog) {
  }

  openResultsInfoDialog(): void {
    const dialogRef = this.dialog.open(OptimalResultsHelpDialogComponent, {
      width: '900px',
    });
  }

  openRunInfoDialog(): void {
    const dialogRef = this.dialog.open(OptimalRunHelpDialogComponent, {
      width: '900px',
    });
  }


  ngOnInit(): void {
    if (!(this.param_script === 'Optimal')) {
      this.datasetService.getDatasetList().subscribe((datasets) => {
        this.datasets = datasets;
        this.runPressed = false;
      });
    } else {
      this.historyPreview();
    }
  }

  atLeastOneAttribute(): boolean {
    return this.selectedAttributes.some(attribute => attribute);
  }

  historyPreview() {
    const getDatasetList$ = this.datasetService.getDatasetList();

    forkJoin([getDatasetList$]).subscribe(([datasets]) => {
      this.datasets = datasets;
      this.selectedDataset = this.datasets.find(dataset =>
        dataset.dataset === this.param_dataset && dataset.access_type === this.param_accessType
      );

      this.sampleToggle = this.param_sample;
      const readDataset$ = this.datasetService.readDataset(this.selectedDataset);

      readDataset$.pipe(
        switchMap((response) => {
          this.availableAttributes = [...response.attributes];
          this.selectedAttributes = new Array(this.availableAttributes.length);
          return of(response);
        })
      ).subscribe(() => {
        this.param_attributes.split(" ").forEach(attribute => {
          const index = this.availableAttributes.indexOf(attribute);

          if (index !== -1) {
            this.selectedAttributes[index] = true;
          }

        });
        this.runAlgorithm();
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

  runAlgorithm(): void {

    if (this.currentTab != 0)
      return;

    if (!this.atLeastOneAttribute())
      return;

    this.loadingResults = true;
    this.loadingParams = true;
    this.runPressed = true;

    const requestData = {
      filename: this.selectedDataset?.dataset,
      access_type: this.selectedDataset?.access_type,
      sample: this.sampleToggle.toString(),
      attributes: this.availableAttributes
        .filter((option, index) => this.selectedAttributes[index])
    };

    this.hierarchicalService.runOptimal(requestData).subscribe({
        next: (result) => {
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
    this.hierarchicalService.runOptimal(requestData).subscribe((result) => {

      if (result.status === 'RUNNING') {
        setTimeout(() => this.pollForStatus(requestData), 1000);
      } else if (result.status === 'FINISHED') {
        this.duration = result.duration;
        this.getOptimalResult(result.id);
      } else if (result.status === 'ERROR') {
        this.error = true;
        console.error('Unexpected status:', result.status);
      }
    });
  }


  private getOptimalResult(id: number): void {
    this.resourceService.getOptimalResults(id).subscribe({
        next: (result) => {
          this.transformChartData(result.all_results);
          this.recommendedLinkage = result.best_linkage;
          this.recommendedNumClusters = result.best_clusters;
          this.loadingResults = false;
          this.loadingParams = false;
          this.customSnackbarService.open('Results fetched Successfully', 'Close', {});

        },
        error: (error) => {
          console.error('Error fetching optimal result:', error);
          this.customSnackbarService.open('Something wend wrong fetching results', 'Close', {});
        }
      }
    );
  }

  private transformChartData(allResults: any[]): void {
    const barChartData = [];
    const barChartLabels = [];
    const linearChartData = [];

    allResults.forEach(result => {
      barChartLabels.push(result.linkage);
      barChartData.push(result.clusters);
      linearChartData.push(result.max_inconsistency);
    });

    this.chartData = [
      {data: barChartData, label: 'Clusters', yAxisID: 'y-axis-0', type: 'bar'},
      {data: linearChartData, label: 'Max Inconsistency', yAxisID: 'y-axis-1', type: 'line'}
    ];

    this.chartLabels = barChartLabels;
  }

  redirectToAnalysis(): void {

    const attributes = this.availableAttributes
      .filter((option, index) => this.selectedAttributes[index]);
    const analysisQueryParams = {
      script: "Analysis",
      dataset: this.selectedDataset.dataset,
      accessType: this.selectedDataset.access_type,
      linkage: this.recommendedLinkage,
      numClusters: this.recommendedNumClusters,
      attributes: attributes.join(' '),
      sample: this.sampleToggle,
    };

    this.router.navigate(['/hierarchical'], {
      queryParams: analysisQueryParams
    });
  }


  openChartDialog(): void {
    const dialogRef = this.dialog.open(ChartDialogComponent, {
      width: '50%',
      data: {
        chartData: this.chartData,
        chartLabels: this.chartLabels,
        chartOptions: this.chartOptions
      }
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }
}
