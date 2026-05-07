package id.ac.ui.cs.advprog.bidmartcatalog.repository;

import id.ac.ui.cs.advprog.bidmartcatalog.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    boolean existsByEventId(String eventId);
}
