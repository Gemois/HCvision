import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {HistoryService} from "../../services/history.service";
import {Router} from "@angular/router";
import {SnackbarService} from "../../services/snackbar.service";

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrl: './history.component.css'
})
export class HistoryComponent implements OnInit {
  historyList: any[] = [];
  expandedItemId: number | undefined;

  constructor(private historyService: HistoryService, private snackbarService: SnackbarService, private router: Router) {

  }

  ngOnInit(): void {
    this.showHistoryList();
  }

  showHistoryList() {
    this.historyService.getHistoryList().subscribe({
        next: (data) => {
          this.historyList = data;
        },
        error: (error) => {
          console.error('Error fetching history:', error);
        }
      }
    );
  }

  deleteHistory(id: number) {
    this.historyService.deleteHistoryById(id).subscribe({
        next: () => {
          this.snackbarService.open('History deleted successfully', 'Close', {});
          console.error('Dataset deleted successfully');
          this.reloadDatasetList()
        },
        error: (error) => {
          this.snackbarService.open('History deleted successfully', 'Close', {});
          console.error('Error deleting dataset:', error);
        }
      }
    );

  }

  panelOpened(item: any): void {
    this.expandedItemId = item.id;
  }

  reloadDatasetList() {
    this.showHistoryList();
  }

}
