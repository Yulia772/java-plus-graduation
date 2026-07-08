package ru.practicum.request.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.QRequest;
import ru.practicum.request.model.Request;
import ru.practicum.interactionapi.dto.request.RequestStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RequestRepositoryImpl extends QuerydslRepositorySupport
        implements RequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QRequest request = QRequest.request;

    public RequestRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Request.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public int confirmedCount(Long eventId) {
        Long count = queryFactory
                .select(request.count())
                .from(request)
                .where(request.eventId.eq(eventId)
                        .and(request.status.eq(RequestStatus.CONFIRMED)))
                .fetchFirst();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public Map<Long, Long> confirmedCount(List<Long> eventIds) {
        List<com.querydsl.core.Tuple> results = queryFactory
                .select(request.eventId, request.count())
                .from(request)
                .where(request.eventId.in(eventIds)
                        .and(request.status.eq(RequestStatus.CONFIRMED)))
                .groupBy(request.eventId)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(request.eventId),
                        tuple -> tuple.get(request.count()),
                        (a, b) -> a + b
                ));
    }

    @Override
    public List<Request> findAllByEventId(Long eventId) {
        return queryFactory
                .selectFrom(request)
                .where(request.eventId.eq(eventId))
                .fetch();
    }

    @Override
    public List<Request> findRequestsByIds(List<Long> ids) {
        return queryFactory
                .selectFrom(request)
                .where(request.id.in(ids))
                .orderBy(request.created.asc())
                .fetch();
    }

    @Override
    public List<Request> findAllByRequesterId(Long requesterId) {
        return queryFactory
                .selectFrom(request)
                .where(request.requesterId.eq(requesterId))
                .fetch();
    }

    @Override
    public boolean existsByRequesterIdAndEventId(Long userId, Long eventId) {
        return queryFactory
                .selectFrom(request)
                .where(request.requesterId.eq(userId)
                        .and(request.eventId.eq(eventId)))
                .fetchFirst() != null;
    }

    @Override
    public List<Request> findRequestsByStatusAndEvent(RequestStatus status, Long eventId) {
        return queryFactory
                .selectFrom(request)
                .where(request.status.eq(status)
                        .and(request.eventId.eq(eventId)))
                .fetch();
    }
}