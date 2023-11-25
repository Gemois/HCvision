import { Component } from '@angular/core';

@Component({
  selector: 'app-hierarchical',
  templateUrl: './hierarchical.component.html',
  styleUrl: './hierarchical.component.css'
})
export class HierarchicalComponent {

  selectedTab: string = 'first';

  selectTab(tab: string): void {
    this.selectedTab = tab;
  }


}
