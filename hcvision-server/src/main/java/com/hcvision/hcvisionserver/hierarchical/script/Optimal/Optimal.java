package com.hcvision.hcvisionserver.hierarchical.script.Optimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.hierarchical.script.ResultStatus;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import com.hcvision.hcvisionserver.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Optimal implements PythonScript {

    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(nullable = false, name = "_user_id")
    private User user;
    @ManyToOne
    @JoinColumn(nullable = false, name = "dataset_id")
    private Dataset dataset;

    private int maxClusters;

    private boolean sample;

    private String attributes;

    @Enumerated
    private ResultStatus status;

    private String result;

    public Optimal(User user, Dataset dataset, int maxClusters, boolean isSample, String attributes, ResultStatus status) {
        this.user = user;
        this.dataset = dataset;
        this.maxClusters = maxClusters;
        this.sample = isSample;
        this.attributes = attributes;
        this.status = status;
    }

    @Override
    public String getScriptDirName() {
        return "optimal";
    }

    public String getOptimalParamsResultFileName() {
        return "optimal_params.json";
    }

    public List<String> getAttributes() {
        return List.of(attributes.split(","));
    }

    public interface ProjectOptimal {
        @JsonProperty("id")
        Long getId();

        @JsonProperty("status")
        ResultStatus getStatus();

        @JsonProperty("dataset")
        Dataset.ProjectNameAndAccessType getDataset();

        @JsonProperty("max_clusters")
        int getMaxClusters();

        @JsonProperty("sample")
        boolean isSample();

        @JsonProperty("attributes")
        List<String> getAttributes();

        @JsonProperty("silhouette")
        String getResult();
    }

}
