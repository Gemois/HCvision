import pandas as pd
from sklearn.cluster import AgglomerativeClustering
from sklearn.metrics import silhouette_score
import argparse
import json
import os

def find_best_linkage_and_clusters(result_path, dataset_path, max_clusters, sampling, attributes):
    if dataset_path.endswith('.csv'):
        if sampling:
            dataset = pd.read_csv(dataset_path, nrows=1000)
        else:
            dataset = pd.read_csv(dataset_path)
    else:
        if sampling:
            dataset = pd.read_excel(dataset_path, nrows=1000)
        else:
            dataset = pd.read_excel(dataset_path)

    selected_attributes = dataset[attributes]

    all_results = []
    best_score = -1
    best_linkage = None
    best_clusters = None

    max_clusters = min(max_clusters, 15)

    for linkage in ['ward', 'complete', 'average', 'single']:
        for n_clusters in range(2, max_clusters + 1):
            clustering = AgglomerativeClustering(n_clusters=n_clusters, linkage=linkage)
            cluster_labels = clustering.fit_predict(selected_attributes)
            score = silhouette_score(selected_attributes, cluster_labels)

            result = {
                "linkage": linkage,
                "clusters": n_clusters,
                "score": score
            }

            all_results.append(result)

            if score > best_score:
                best_score = score
                best_linkage = linkage
                best_clusters = n_clusters

    result = {
        "all_results": all_results,
        "best_linkage": best_linkage,
        "best_clusters": best_clusters,
        "best_score": best_score
    }

    predict_json_path = os.path.join(result_path, "optimal_params.json")
    with open(predict_json_path, 'w') as f:
        json.dump(result, f, indent=4)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("result_path", type=str)
    parser.add_argument("dataset_path", type=str)
    parser.add_argument("max_clusters", type=int)
    parser.add_argument("--sampling", action="store_true", default=False)
    parser.add_argument("attributes", type=str, nargs='+')
    args = parser.parse_args()
    find_best_linkage_and_clusters(args.result_path, args.dataset_path, args.max_clusters, args.sampling, args.attributes)
