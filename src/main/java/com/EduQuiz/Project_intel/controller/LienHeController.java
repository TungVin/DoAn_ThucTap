package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.LienHe;
import com.EduQuiz.Project_intel.service.LienHeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/lien-he")
public class LienHeController {

    private final LienHeService lienHeService;

    public LienHeController(LienHeService lienHeService) {
        this.lienHeService = lienHeService;
    }

    @GetMapping
    public String hienThiTrangLienHe() {
        return "ChucNang/LienHe";
    }

    @PostMapping("/gui")
    public String guiLienHe(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam String message
    ) {
        LienHe lh = new LienHe();
        lh.setHoTen(name);
        lh.setEmail(email);
        lh.setSoDienThoai(phone);
        lh.setNoiDung(message);

        lienHeService.save(lh);

        return "LienHeSuccess";
    }
}
