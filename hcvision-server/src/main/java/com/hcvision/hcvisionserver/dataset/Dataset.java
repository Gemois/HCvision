package com.hcvision.hcvisionserver.dataset;

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
    private com.hcvision.hcvisionserver.dataset.dto.AccessType accessType;
    private String path;


    public Dataset(User user, String name, AccessType accessType, String path) {
        this.user = user;
        this.fileName = name;
        this.accessType = accessType;
        this.path = path;
    }

    public interface ProjectNameAndAccessType {
        String getFileName();

        String getAccessType();
    }

}
