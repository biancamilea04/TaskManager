package org.example.projectjava.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Controller
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/requestNotification")
    public void sendMessageIfDayMatches(StompHeaderAccessor sha) {
        HttpSession session = (HttpSession) sha.getSessionAttributes().get("HTTP_SESSION");

        if (session == null) return;

        Boolean alreadySent = (Boolean) session.getAttribute("notificationSent");
        if (Boolean.TRUE.equals(alreadySent)) return;

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        if (today == DayOfWeek.MONDAY || today == DayOfWeek.TUESDAY || today == DayOfWeek.WEDNESDAY) {
            messagingTemplate.convertAndSend("/topic/special", "Miercuri la 20:00 este AG");
            session.setAttribute("notificationSent", true); // marcheză că a fost trimis
        }
    }

}


