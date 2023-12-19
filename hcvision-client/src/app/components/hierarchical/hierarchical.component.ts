import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {event} from "jquery";

@Component({
  selector: 'app-hierarchical',
  templateUrl: './hierarchical.component.html',
  styleUrl: './hierarchical.component.css'
})
export class HierarchicalComponent implements OnInit {
  script: string;
  dataset: string;
  accessType: string;
  linkage: string;
  numClusters: number;
  attributes: string;
  sample: boolean;
  selectedIndex: number = 0;


  constructor(private route: ActivatedRoute, private router: Router) {

  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params && Object.keys(params).length > 0) {
        console.log("I'm here");
        this.script = params['script'];
        this.dataset = params['dataset'];
        this.accessType = params['accessType'];
        this.linkage = params['linkage'];
        this.numClusters = params['numClusters'];
        this.attributes = params['attributes'];
        this.sample = params['sample'];
        this.selectedIndex = this.script === 'Optimal' ? 0 : 1;
      }
    });
  }

  selectTab(tab: string): void {
    this.selectedIndex = tab === 'first' ? 0 : 1;
  }


  removeQueryParams() {
    const currentUrlTree = this.router.createUrlTree([], {relativeTo: this.route});
    this.router.navigate([currentUrlTree.toString()]);

  }


  protected readonly event = event;

}
