package com.hcvision.hcvisionserver.hierarchical.script.analysis;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.hierarchical.script.Linkage;
import com.hcvision.hcvisionserver.hierarchical.script.ResultStatus;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import com.hcvision.hcvisionserver.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Analysis implements PythonScript {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false, name = "dataset_id")
    private Dataset dataset;

    private Linkage linkage;

    private int numClusters;

    private boolean sample;

    private String attributes;

    @Enumerated
    private ResultStatus status;

    private String dendrogramResultPath;

    private String parallelCoordinatesResultPath;

    private String clusterAssignmentResultPath;

    public Analysis(User user, Dataset dataset, Linkage linkage, int numClusters, boolean sample, String attributes, ResultStatus status) {
        this.user = user;
        this.dataset = dataset;
        this.linkage = linkage;
        this.numClusters = numClusters;
        this.sample = sample;
        this.attributes = attributes;
        this.status = status;
    }
    @Override
    public String getScriptDirName() {
        return "analysis";
    }

    public String getDendrogramResultFileName() {
        return "dendrogram.png";
    }

    public String getParallelCoordinatesResultFileName() {
        return "parallel_coordinates.png";
    }

    public String getClusterAssignmentResultFileName() {
        return "cluster_assignments.png";
    }

    public interface ProjectAnalysis {
        ResultStatus getStatus();
        Dataset.ProjectNameAndAccessType getDataset();
        Linkage getLinkage();
        int getNumClusters();
        boolean getSample();
        String dendrogramResultPath();
        String getDendrogramResultPath();
        String getParallelCoordinatesResultPath();
        String getClusterAssignmentResultPath();
    }


}






