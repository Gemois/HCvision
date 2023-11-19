package com.hcvision.hcvisionserver.hierarchical.History;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hcvision.hcvisionserver.hierarchical.script.Optimal.Optimal;
import com.hcvision.hcvisionserver.hierarchical.script.analysis.Analysis;
import com.hcvision.hcvisionserver.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class History {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime timeStarted;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "optimal_id")
    private Optimal optimal;

    @OneToOne
    @JoinColumn(name = "analysis_id")
    private Analysis analysis;

    public History(LocalDateTime timeStarted, User user, Optimal optimal) {
        this.timeStarted = timeStarted;
        this.user = user;
        this.optimal = optimal;
        this.currentScript = "Optimal";
    }

    public History(LocalDateTime timeStarted, User user, Analysis analysis) {
        this.timeStarted = timeStarted;
        this.user = user;
        this.analysis = analysis;
        this.currentScript = "Analysis";
    }

    private String currentScript;

    @JsonPropertyOrder({"id", "current_script", "Started_at", "optimal", "analysis"})
    public interface ProjectHistory {
        @JsonProperty("id")
        String getId();

        @JsonProperty("current_script")
        String getCurrentScript();

        @JsonProperty("Started_at")
        LocalDateTime getTimeStarted();

        @JsonProperty("optimal")
        Optimal.ProjectOptimal getOptimal();

        @JsonProperty("analysis")
        Analysis.ProjectAnalysis getAnalysis();
    }

    @JsonPropertyOrder({"id", "script", "Started_at"})
    public interface ProjectHistoryList {
        @JsonProperty("id")
        String getId();

        @JsonProperty("script")
        String getCurrentScript();

        @JsonProperty("started_at")
        LocalDateTime getTimeStarted();
    }
}
