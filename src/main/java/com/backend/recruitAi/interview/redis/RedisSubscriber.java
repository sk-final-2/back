package com.backend.recruitAi.interview.redis;

import com.backend.recruitAi.interview.controller.InterviewSocketController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final InterviewSocketController socketController;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        log.info("Redis 수신 - 채널: {}, 메시지: {}", channel, body);

        // ex: interview:abcd-1234:done → extract interviewId
        String interviewId = extractInterviewId(channel);
        socketController.sendAnalysisComplete(interviewId);
    }

    private String extractInterviewId(String channel) {
        return channel.replace("interview:", "").replace(":done", "");
    }
}
