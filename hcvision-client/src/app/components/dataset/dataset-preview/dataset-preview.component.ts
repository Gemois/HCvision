import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";

@Component({
  selector: 'app-dataset-preview',
  templateUrl: './dataset-preview.component.html',
  styleUrl: './dataset-preview.component.css'
})
export class DatasetPreviewComponent implements OnInit, OnChanges {
  @Input() jsonData: any[];

  dataSource: MatTableDataSource<any>;
  displayedColumns: string[];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  isHidden = true;

  ngOnInit(): void {
    if (this.jsonData && this.jsonData.length > 0) {
      this.isHidden = true;
      this.displayedColumns = Object.keys(this.jsonData[0]);
      this.dataSource = new MatTableDataSource(this.jsonData);
      this.dataSource.paginator = this.paginator;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.jsonData && this.jsonData.length > 0 && changes.jsonData) {
      this.isHidden = false;
      this.displayedColumns = Object.keys(this.jsonData[0]);
      this.dataSource = new MatTableDataSource(this.jsonData);
      this.dataSource.paginator = this.paginator;
    }
  }
}
