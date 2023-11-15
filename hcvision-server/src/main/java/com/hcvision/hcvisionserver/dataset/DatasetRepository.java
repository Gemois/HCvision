package com.hcvision.hcvisionserver.dataset;


import com.hcvision.hcvisionserver.dataset.dto.AccessType;
import com.hcvision.hcvisionserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {

    Dataset findByFileNameAndAccessTypeAndUser(String fileName, AccessType accessType, User user);

    Dataset findByAccessTypeAndFileName(AccessType accessType, String fileName);

    List<Dataset> findByUserAndAccessType(User user, AccessType accessType);

    List<Dataset.ProjectNameAndAccessType> findAllByUser(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Dataset s WHERE s.user = ?1")
    void deleteAllUserDatasets(User user);

}
