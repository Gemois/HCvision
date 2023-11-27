import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {HistoryService} from "../../../services/history.service";
import {Router} from "@angular/router";
import {SnackbarService} from "../../../services/snackbar.service";

@Component({
  selector: 'app-history-preview',
  templateUrl: './history-preview.component.html',
  styleUrl: './history-preview.component.css'
})
export class HistoryPreviewComponent implements OnInit {
  @Input() id: number | null;
  @Input() script: string | null;
  @Output() deleteHistoryEvent = new EventEmitter<number>();

  optimalDetails: any = {};
  analysisDetails: any = {};

  constructor(private historyService: HistoryService, private router: Router, private snackbarService: SnackbarService) {
  }

  ngOnInit(): void {
    this.initSinglePanel();
  }


  initSinglePanel() {
    if (this.id && this.script) {
      this.historyService.getHistoryById(this.id).subscribe(
        (data) => {
          if (this.script === 'Optimal') {
            this.optimalDetails = data.optimal;
          } else if (this.script === 'Analysis') {
            this.analysisDetails = data.analysis;
          }
        },
        (error) => {
          console.error('Error fetching history details:', error);
        }
      );
    }
  }

  deleteHistoryById(id: number) {
    this.deleteHistoryEvent.emit(id);
  }

  redirectToHierarchical(): void {
    const optimalQueryParams = {
      script: this.script,
      dataset: this.optimalDetails?.dataset?.dataset,
      accessType: this.optimalDetails?.dataset?.access_type,
      maxClusters: this.optimalDetails?.max_clusters,
      attributes: this.optimalDetails?.attributes?.join(','),
      sample: this.optimalDetails?.sample,
    };

    const analysisQueryParams = {
      script: this.script,
      dataset: this.analysisDetails?.dataset?.dataset,
      accessType: this.analysisDetails?.dataset?.access_type,
      linkage: this.analysisDetails?.linkage,
      numClusters: this.analysisDetails?.n_clusters,
      attributes: this.analysisDetails?.attributes?.join(','),
      sample: this.analysisDetails?.sample,
    };

    this.router.navigate(['/hierarchical'], {
      queryParams: this.script === 'Optimal' ? optimalQueryParams : analysisQueryParams,
    });
  }


}
