import pandas as pd
from pandas.plotting import parallel_coordinates
from scipy.cluster.hierarchy import dendrogram, linkage
from sklearn.preprocessing import StandardScaler
from sklearn.cluster import AgglomerativeClustering
import matplotlib.pyplot as plt
import matplotlib.colors as mcolors
import argparse
import os

def perform_hierarchical_clustering(result_path, dataset_path, linkage_type, num_clusters, sampling, attributes):
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

    selected_data = dataset[attributes]
    data_standardized = StandardScaler().fit_transform(selected_data)

    clustering = AgglomerativeClustering(n_clusters=num_clusters, linkage=linkage_type)
    cluster_labels = clustering.fit_predict(data_standardized)
    dataset['Cluster_Labels'] = cluster_labels

    linkage_matrix = linkage(data_standardized, method=linkage_type)

    plt.figure(figsize=(15, 8))
    dendrogram(linkage_matrix, labels=cluster_labels, color_threshold=1.5)
    plt.title(f'{linkage_type} Hierarchical Clustering Dendrogram')
    dendrogram_path = os.path.join(result_path, "dendrogram.png")
    plt.savefig(dendrogram_path)
    plt.close()

    custom_cmap = plt.get_cmap('viridis')
    colors = [custom_cmap(i / num_clusters) for i in range(num_clusters)]
    hex_colors = [mcolors.to_hex(color) for color in colors]    
    if len(selected_data) > 1:
        plt.figure(figsize=(15, 8))
        parallel_coordinates(selected_data.assign(Cluster_Labels=dataset['Cluster_Labels']), 'Cluster_Labels', color=hex_colors)
        plt.title('Parallel Coordinates Plot')
        parallel_coordinates_path = os.path.join(result_path, "parallel_coordinates.png")
        plt.savefig(parallel_coordinates_path)
        plt.close()

    dataset['Cluster_Labels'] = cluster_labels
    json_data = dataset.to_json(orient='records')

    analysis_json_path = os.path.join(result_path, "cluster_assignments.json")
    with open(analysis_json_path, 'w') as json_file:
        json_file.write(json_data)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("result_path")
    parser.add_argument("dataset_path")
    parser.add_argument("linkage_type")
    parser.add_argument("num_clusters", type=int)
    parser.add_argument("sampling")
    parser.add_argument("attributes", nargs='+')
   
    args = parser.parse_args()
    perform_hierarchical_clustering(args.result_path, args.dataset_path, args.linkage_type, args.num_clusters, args.sampling, args.attributes)
