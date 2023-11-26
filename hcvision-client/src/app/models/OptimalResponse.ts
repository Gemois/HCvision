import {SilhouetteCombo} from "./SilhouetteCombo";

export class OptimalResponse {
  constructor(
    public all_results: SilhouetteCombo[],
    public best_linkage: string,
    public best_clusters: number,
    public best_score: number,
  ) {
  }
}
