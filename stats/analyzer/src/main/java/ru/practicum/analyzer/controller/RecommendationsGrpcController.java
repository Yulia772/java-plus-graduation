package ru.practicum.analyzer.controller;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.analyzer.dto.EventScore;
import ru.practicum.analyzer.service.RecommendationService;
import ru.practicum.ewm.stats.proto.dashboard.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.message.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.message.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.message.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.message.UserPredictionsRequestProto;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class RecommendationsGrpcController
        extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            List<EventScore> recommendations = recommendationService.getRecommendationsForUser(
                    request.getUserId(),
                    request.getMaxResults()
            );

            sendResponse(recommendations, responseObserver);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Ошибка при получении рекомендаций пользователя")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            List<EventScore> similarEvents = recommendationService.getSimilarEvents(
                    request.getEventId(),
                    request.getUserId(),
                    request.getMaxResults()
            );

            sendResponse(similarEvents, responseObserver);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Ошибка при получении похожих мероприятий")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            List<EventScore> interactionsCount = recommendationService.getInteractionsCount(
                    request.getEventIdList()
            );

            sendResponse(interactionsCount, responseObserver);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Ошибка при получении количества взаимодействий")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    private void sendResponse(List<EventScore> eventScores,
                              StreamObserver<RecommendedEventProto> responseObserver) {
        for (EventScore eventScore : eventScores) {
            responseObserver.onNext(toProto(eventScore));
        }

        responseObserver.onCompleted();
    }

    private RecommendedEventProto toProto(EventScore eventScore) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventScore.getEventId())
                .setScore(eventScore.getScore())
                .build();
    }
}