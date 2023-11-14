package com.hcvision.hcvisionserver.hierarchical.History;

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
    }
    public History(LocalDateTime timeStarted, User user, Analysis analysis) {
        this.timeStarted = timeStarted;
        this.user = user;
        this.analysis = analysis;
    }
}
