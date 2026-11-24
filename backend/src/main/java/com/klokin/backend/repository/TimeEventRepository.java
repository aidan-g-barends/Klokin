package com.klokin.backend.repository;

import com.klokin.backend.domain.TimeEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TimeEventRepository extends JpaRepository<TimeEvent, Long> {

    boolean existsByEventUuid(String eventUuid);

    // JPQL: like SQL, but written against our classes and fields instead of tables and columns.
    // >= start and < end, so an event at exactly midnight lands in one day only.
    @Query("select e from TimeEvent e where e.employee.id = :employeeId "
            + "and e.occurredAt >= :startTime and e.occurredAt < :endTime order by e.occurredAt")
    List<TimeEvent> findForEmployeeBetween(@Param("employeeId") Long employeeId,
                                           @Param("startTime") Instant startTime,
                                           @Param("endTime") Instant endTime);

    // "join fetch" loads each event's employee and client in the same query.
    // Without it, Hibernate would run one extra query per event (the "N+1 problem").
    @Query("select e from TimeEvent e join fetch e.employee emp left join fetch e.client "
            + "where emp.company.id = :companyId order by e.occurredAt desc")
    List<TimeEvent> findRecentForCompany(@Param("companyId") Long companyId, Pageable pageable);
}
