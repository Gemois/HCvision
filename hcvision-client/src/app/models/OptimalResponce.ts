import {SilhouetteCombo} from "./SilhouetteCombo";

export class OptimalResponce {
  constructor(
    public all_results: SilhouetteCombo[],
    public best_linkage: string,
    public best_clusters: number,
    public best_score: number,
  ) {
  }
}
