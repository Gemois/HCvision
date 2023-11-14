package com.hcvision.hcvisionserver.hierarchical.script.Optimal;

import com.hcvision.hcvisionserver.dataset.Dataset;
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

    public Optimal(User user, Dataset dataset, int maxClusters, boolean sample, String attributes, ResultStatus status) {
        this.user = user;
        this.dataset = dataset;
        this.maxClusters = maxClusters;
        this.sample = sample;
        this.attributes = attributes;
        this.status = status;
    }

    private boolean sample;

    private String attributes;

    @Enumerated
    private ResultStatus status;

    private String result;

    @Override
    public String getScriptDirName() {
        return "optimal";
    }

    public interface ProjectOptimal {
        ResultStatus getStatus();
        Dataset.ProjectNameAndAccessType getDataset();
        int getMaxClusters();
        boolean getSample();
        String getAttributes();
        String getResult();
    }

}
