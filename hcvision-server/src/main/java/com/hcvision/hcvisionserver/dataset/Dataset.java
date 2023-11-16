package com.hcvision.hcvisionserver.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcvision.hcvisionserver.dataset.dto.AccessType;
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
public class Dataset {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(nullable = false, name = "_user_id")
    private User user;
    private String fileName;
    private AccessType accessType;
    private String path;
    private String numericCols;

    public Dataset(User user, String name, AccessType accessType, String path, String numericCols) {
        this.user = user;
        this.fileName = name;
        this.accessType = accessType;
        this.path = path;
        this.numericCols = numericCols;
    }

    public interface ProjectNameAndAccessType {
        @JsonProperty("dataset")
        String getFileName();

        @JsonProperty("access_type")
        String getAccessType();
    }

}
