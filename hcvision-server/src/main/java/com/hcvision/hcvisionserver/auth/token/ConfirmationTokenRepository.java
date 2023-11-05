package com.hcvision.hcvisionserver.auth.token;

import com.hcvision.hcvisionserver.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " + "SET c.confirmedAt = ?2 " + "WHERE c.token = ?1")
    void updateConfirmedAt(String token, LocalDateTime confirmedAt);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " + "SET c.confirmedAt = ?2 " + "WHERE c.user = ?1")
    void retireTokens(User user, LocalDateTime now);
}
