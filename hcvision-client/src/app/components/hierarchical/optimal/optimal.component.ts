import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Dataset} from "../../../models/Dataset";
import {HierarchicalService} from "../../../services/hierarchical.service";
import {DatasetService} from "../../../services/dataset.service";
import {ResourceService} from "../../../services/resource.service";
import {SilhouetteCombo} from "../../../models/SilhouetteCombo";
import {SnackbarService} from "../../../services/snackbar.service";
import {forkJoin, of, switchMap} from "rxjs";
import {Router} from "@angular/router";

@Component({
  selector: 'app-optimal',
  templateUrl: './optimal.component.html',
  styleUrl: './optimal.component.css'
})
export class OptimalComponent implements OnInit {
  @Input() param_script: string;
  @Input() param_dataset: string;
  @Input() param_accessType: string;
  @Input() param_maxClusters: number;
  @Input() param_attributes: string;
  @Input() param_sample: boolean;

  datasets: Dataset[] = [];
  selectedDataset: Dataset | null = null;
  maxClusters: number = 0;
  sampleToggle: boolean = true;
  availableAttributes: any[] = [];
  selectedAttributes: any[] = [];

  silhouetteCombos: any[] = [];
  chartData: any[] = [];
  chartLabels: string[] = [];
  chartOptions: any = {
    responsive: true
  };
  recommendedLinkage: string | null = null;
  recommendedNumClusters: number | null = null;

  runPressed: boolean = false;
  loadingResults: boolean = true;
  loadingParams: boolean = true;

  constructor(private hierarchicalService: HierarchicalService,
              private datasetService: DatasetService,
              private resourceService: ResourceService,
              private customSnackbarService: SnackbarService, private router:Router) {
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

  historyPreview() {
    const getDatasetList$ = this.datasetService.getDatasetList();

    forkJoin([getDatasetList$]).subscribe(([datasets]) => {
      this.datasets = datasets;
      this.selectedDataset = this.datasets.find(dataset =>
        dataset.dataset === this.param_dataset && dataset.access_type === this.param_accessType
      );

      this.maxClusters = this.param_maxClusters;
      this.sampleToggle = this.param_sample;

      const readDataset$ = this.datasetService.readDataset(this.selectedDataset);

      readDataset$.pipe(
        switchMap((response) => {
          this.availableAttributes = [...response.attributes];
          this.selectedAttributes = new Array(this.availableAttributes.length).fill(false);
          this.param_attributes.split(",").forEach(attribute => {
            const index = this.availableAttributes.indexOf(attribute);
            if (index !== -1) {
              this.selectedAttributes[index] = true;
            }
          });

          return of(response);
        })
      ).subscribe(() => {
        this.runAlgorithm();
      });
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

  runAlgorithm(): void {
    this.loadingResults = true;
    this.loadingParams = true;
    this.runPressed = true;

    const requestData = {
      filename: this.selectedDataset?.dataset,
      access_type: this.selectedDataset?.access_type,
      max_clusters: this.maxClusters.toString(),
      sample: this.sampleToggle.toString(),
      attributes: this.availableAttributes
        .filter((option, index) => this.selectedAttributes[index])
    };

    this.hierarchicalService.runOptimal(requestData).subscribe({
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
    this.hierarchicalService.runOptimal(requestData).subscribe((result) => {
      console.log('Result:', result);

      if (result.status === 'RUNNING') {
        setTimeout(() => this.pollForStatus(requestData), 1000); // Adjust the delay as needed
      } else if (result.status === 'FINISHED') {
        console.log('Operation finished successfully!');
        this.getOptimalResult(result.id);
      } else {
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

  private transformChartData(allResults: SilhouetteCombo[]): void {
    const groupByLinkageAndClusters: { [key: string]: { [key: string]: number } } = {};

    allResults.forEach((result) => {
      const linkage = result.linkage;
      const clusters = result.clusters;
      const score = result.score;

      if (!groupByLinkageAndClusters[linkage]) {
        groupByLinkageAndClusters[linkage] = {};
      }

      groupByLinkageAndClusters[linkage][clusters] = score;
    });

    this.silhouetteCombos = Object.keys(groupByLinkageAndClusters).map((linkage) => {
      return {
        data: Object.values(groupByLinkageAndClusters[linkage]),
        label: linkage
      };
    });

    const clusters = Object.keys(groupByLinkageAndClusters[Object.keys(groupByLinkageAndClusters)[0]] || {});

    this.chartData = this.silhouetteCombos;
    this.chartLabels = clusters;
  }


  redirectToAnalysis(): void {

    const selectedAttributes = this.availableAttributes
      .filter((option, index) => this.selectedAttributes[index]);
    const analysisQueryParams = {
      script: "Analysis",
      dataset: this.selectedDataset.dataset,
      accessType: this.selectedDataset.access_type,
      linkage: this.recommendedLinkage,
      numClusters: this.recommendedNumClusters,
      attributes: selectedAttributes.join(','),
      sample: this.sampleToggle,
    };

    this.router.navigate(['/hierarchical'], {
      queryParams: analysisQueryParams
    });
  }



}
