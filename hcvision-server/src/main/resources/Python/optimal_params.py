import pandas as pd
import argparse
import json
import os
from scipy.cluster.hierarchy import linkage, fcluster
import numpy as np

def find_optimal_clusters(Z, max_d):
    clusters = fcluster(Z, t=max_d, criterion='distance')
    return len(np.unique(clusters))

def find_best_linkage_and_clusters(result_path, dataset_path, sampling, attributes):
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
    best_linkage = None
    best_clusters = 0

    for linkage_method in ['ward', 'complete', 'average', 'single']:
        Z = linkage(selected_attributes, linkage_method)
        distances = Z[:, 2]
        diff_distances = np.diff(distances, 2)
        max_d = distances[np.argmax(diff_distances)]

        n_clusters = find_optimal_clusters(Z, max_d)

        result = {
            "linkage": linkage_method,
            "clusters": n_clusters,
            "max_inconsistency": max_d
        }

        all_results.append(result)

        if n_clusters > best_clusters and max_d > 0:
            best_linkage = linkage_method
            best_clusters = n_clusters

    result = {
        "all_results": all_results,
        "best_linkage": best_linkage,
        "best_clusters": best_clusters
    }

    predict_json_path = os.path.join(result_path, "optimal_params.json")
    with open(predict_json_path, 'w') as f:
        json.dump(result, f, indent=4)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("result_path", type=str)
    parser.add_argument("dataset_path", type=str)
    parser.add_argument("--sampling", action="store_true", default=False)
    parser.add_argument("attributes", type=str, nargs='+')
    args = parser.parse_args()
    find_best_linkage_and_clusters(args.result_path, args.dataset_path, args.sampling, args.attributes)
