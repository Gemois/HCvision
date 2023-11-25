import {Component, OnInit} from '@angular/core';
import {Dataset} from "../../../models/Dataset";
import {HierarchicalService} from "../../../services/hierarchical.service";
import {DatasetService} from "../../../services/dataset/dataset.service";
import {ResourceService} from "../../../services/resource.service";
import {SilhouetteCombo} from "../../../models/SilhouetteCombo";
import {CustomSnackbarService} from "../../../services/custom-snackbar.service";

@Component({
  selector: 'app-optimal',
  templateUrl: './optimal.component.html',
  styleUrl: './optimal.component.css'
})
export class OptimalComponent implements OnInit {

  datasets: Dataset[] = [];
  selectedDataset: Dataset | null = null;
  maxClusters: number = 0;
  sampleToggle: boolean = false;
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
              private customSnackbarService: CustomSnackbarService) {
  }

  ngOnInit(): void {
    this.datasetService.getDatasetList().subscribe((datasets) => {
      this.datasets = datasets;
    });

  }

  onDatasetSelect(): void {
    console.log(this.selectedDataset);
    console.log(this.maxClusters);
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
    this.resourceService.getOptimalResult(id).subscribe({
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

}
