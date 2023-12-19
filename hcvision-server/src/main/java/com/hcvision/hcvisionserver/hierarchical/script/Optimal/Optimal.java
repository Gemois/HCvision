package com.hcvision.hcvisionserver.hierarchical.script.Optimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.hierarchical.script.ResultStatus;
import com.hcvision.hcvisionserver.hierarchical.script.PythonScript;
import com.hcvision.hcvisionserver.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
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

    private boolean sample;

    private String attributes;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private long duration;

    @Enumerated
    private ResultStatus status;

    private String inconsistencyCoefficient;

    public Optimal(User user, Dataset dataset, boolean isSample, String attributes, ResultStatus status) {
        this.user = user;
        this.dataset = dataset;
        this.sample = isSample;
        this.attributes = attributes;
        this.status = status;
    }

    public void calcDuration() {
        this.duration = Duration.between(startedAt, endedAt).getSeconds();
    }

    @Override
    public String getScriptDirName() {
        return "optimal";
    }

    public static String getOptimalParamsResultFileName() {
        return "optimal_params.json";
    }

    public List<String> getAttributes() {
        return List.of(attributes.split(","));
    }

    @JsonPropertyOrder({"id", "dataset", "attributes", "sample", "status", "duration", "max-inconsistency"})
    public interface ProjectOptimal {
        @JsonProperty("id")
        Long getId();

        @JsonProperty("dataset")
        Dataset.ProjectNameAndAccessType getDataset();

        @JsonProperty("attributes")
        List<String> getAttributes();

        @JsonProperty("sample")
        boolean isSample();

        @JsonProperty("status")
        ResultStatus getStatus();

        @JsonProperty("duration")
        long getDuration();

        @JsonProperty("max-inconsistency")
        String getInconsistencyCoefficient();
    }

    @JsonPropertyOrder({"id", "status"})
    public interface ProjectOptimalStatus {
        @JsonProperty("id")
        Long getId();

        @JsonProperty("status")
        ResultStatus getStatus();
    }
}
