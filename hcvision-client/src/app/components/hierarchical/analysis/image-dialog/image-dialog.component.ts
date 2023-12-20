import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-image-dialog',
  templateUrl: './image-dialog.component.html',
  styleUrl: './image-dialog.component.css'
})
export class ImageDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { imageUrl: string }) {}



  downloadImage(): void {
    const link = document.createElement('a');
    link.href = this.data.imageUrl;
    link.download = 'image.png'; // Set a default filename or customize as needed
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }



}
