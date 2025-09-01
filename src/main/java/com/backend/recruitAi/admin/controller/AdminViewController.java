package com.backend.recruitAi.admin.controller;

import com.backend.recruitAi.member.dto.MemberListDto;
import com.backend.recruitAi.member.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminViewController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model, @RequestParam(required = false) String query) {
        Pageable pageable = PageRequest.of(0, 10); // 첫 페이지(0)의 10개 항목 요청

        // Page<MemberListDto> 타입으로 변경되었으므로, .getContent()를 사용해 List로 변환
        List<MemberListDto> members = adminService.getAllMembers(query, pageable).getContent();

        model.addAttribute("members", members);
        return "admin/dashboard";
    }
}