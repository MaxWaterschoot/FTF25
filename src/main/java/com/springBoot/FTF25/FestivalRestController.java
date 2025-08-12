package com.springBoot.FTF25;

import domain.Festival;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import service.FestivalService;
import service.ReviewService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festivals")
public class FestivalRestController {

    private final FestivalService festivalService;
    private final ReviewService reviewService;

    @GetMapping
    public List<Festival> list(@RequestParam(required = false) String category,
                               @RequestParam(required = false) String region) {
        return festivalService.findByCategoryAndRegion(category, region);
    }

    @GetMapping("/{id}")
    public Festival get(@PathVariable Long id) {
        return festivalService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Festival not found"));
    }

    @GetMapping("/{id}/availableTickets")
    public Map<String, Integer> available(@PathVariable Long id) {
        int available = festivalService.getAvailableTickets(id);
        return Map.of("festivalId", id.intValue(), "availableTickets", available);
    }

    @GetMapping("/{id}/rating")
    public Map<String, Object> rating(@PathVariable Long id) {
        double avg = reviewService.getAverageRating(id);
        return Map.of("festivalId", id, "averageRating", avg);
    }
}
