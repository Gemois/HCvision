import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-upload-dialog',
  templateUrl: './upload-dialog.component.html',
  styleUrl: './upload-dialog.component.css'
})
export class UploadDialogComponent {
  accessTypes: string[] = ['PUBLIC', 'PRIVATE'];
  selectedFile: File | null = null;

  constructor(
    public dialogRef: MatDialogRef<UploadDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { file:File,accessType : String }
  ) {}

  onNoClick(): void {
    this.dialogRef.close({ file: this.selectedFile, accessType: this.data.accessType });
  }


  handleFileInput(event: any): void {
    const files: FileList = event.target.files;
    if (files.length > 0) {
      this.selectedFile = files[0];
    }
  }
}
