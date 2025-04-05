package app.avatar.repo;

import app.avatar.model.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, UUID> {

    Optional<Avatar> findById(UUID id);

    @Query(
        "select a.id from Avatar a"
    )
    UUID[] findAllIds();

}
