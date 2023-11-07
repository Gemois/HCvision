package com.hcvision.hcvisionserver.dataset;


import com.hcvision.hcvisionserver.dataset.dto.AccessType;
import com.hcvision.hcvisionserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {

    Dataset findByFileNameAndAccessTypeAndUser(String fileName, AccessType accessType, User user);

    Dataset findByAccessTypeAndFileName(AccessType accessType, String fileName);

    List<Dataset.ProjectNameAndAccessType> findAllByUser(User user);

}
