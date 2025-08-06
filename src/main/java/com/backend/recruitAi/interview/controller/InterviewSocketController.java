package com.backend.recruitAi.interview.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class InterviewSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendAnalysisComplete(String interviewId) {
        // 구독자에게 메시지 전송 (프론트에서 /topic/interview/{interviewId} 구독해야 수신됨)
        messagingTemplate.convertAndSend("/topic/interview/" + interviewId, "분석이 완료되었습니다!");
    }
}
