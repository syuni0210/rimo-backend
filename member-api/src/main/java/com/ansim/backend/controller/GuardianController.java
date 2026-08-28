package com.ansim.backend.controller;

import com.ansim.backend.entity.Guardian;
import com.ansim.backend.service.GuardianService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/guardians")
@RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardianService;

    @PostMapping
    public Guardian registerGuardian(@RequestBody GuardianRequest request) {
        return guardianService.registerGuardian(
                request.getMemberId(),
                request.getGuardianName(),
                request.getPhoneNumber(),
                request.getRelationName()
        );
    }

    @GetMapping
    public List<Guardian> getGuardians(@RequestParam Long memberId) {
        return guardianService.getGuardians(memberId);
    }

    @PutMapping("/{guardianId}")
    public Guardian updateGuardian(@PathVariable Long guardianId, @RequestBody GuardianRequest request) {
        return guardianService.updateGuardian(
                guardianId,
                request.getGuardianName(),
                request.getPhoneNumber(),
                request.getRelationName()
        );
    }

    @DeleteMapping("/{guardianId}")
    public void deleteGuardian(@PathVariable Long guardianId) {
        guardianService.deleteGuardian(guardianId);
    }
}
