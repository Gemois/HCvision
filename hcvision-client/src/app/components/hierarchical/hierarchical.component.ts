import {Component} from '@angular/core';

@Component({
  selector: 'app-hierarchical',
  templateUrl: './hierarchical.component.html',
  styleUrl: './hierarchical.component.css'
})
export class HierarchicalComponent {

  constructor() {}

  selectedTab: string = 'first';

  selectTab(tab: string): void {
    console.log(tab)
    this.selectedTab = tab;
  }

}
