package com.springBoot.FTF25;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import domain.Festival;
import domain.Standhouder;
import dto.FestivalForm;
import repository.FestivalRepository;
import repository.CategoryRepository;
import repository.LocationRepository;
import repository.StandhouderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/festivals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFestivalController {

    private final FestivalRepository festivalRepo;
    private final CategoryRepository categoryRepo;
    private final LocationRepository locationRepo;
    private final StandhouderRepository standhouderRepo;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("festivals", festivalRepo.findAll());
        return "admin/festivals-list"; // templates/admin/festivals-list.html
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Festival f = festivalRepo.findById(id).orElseThrow();
        FestivalForm form = new FestivalForm();
        form.setFestivalId(f.getFestivalId());
        form.setName(f.getName());
        form.setStartDateTime(f.getStartDateTime());
        form.setAvailableTickets(f.getAvailableTickets());
        form.setTicketPrice(f.getTicketPrice());
        form.setCategory(f.getCategory().getCategoryId());
        form.setLocation(f.getLocation().getLocationId());
        form.setStandhouderIds(
            (f.getStandhouders() == null ? List.<Standhouder>of() : f.getStandhouders())
                .stream()
                .map(Standhouder::getStandhouderId)
                .collect(Collectors.toList())
        );

        model.addAttribute("festival", form); // zelfde attribuutnaam als in je form
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("regions", null); // laad je regio’s indien nodig
        model.addAttribute("locations", locationRepo.findAll());
        model.addAttribute("standhouders", standhouderRepo.findAll());
        return "admin/festival-form"; // <-- juiste pad/naam
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("festival") FestivalForm form) {
        Festival f = festivalRepo.findById(id).orElseThrow();

        f.setName(form.getName());
        f.setStartDateTime(form.getStartDateTime());
        f.setAvailableTickets(form.getAvailableTickets());
        f.setTicketPrice(form.getTicketPrice());
        f.setCategory(categoryRepo.findById(form.getCategory()).orElseThrow());
        f.setLocation(locationRepo.findById(form.getLocation()).orElseThrow());

        List<Standhouder> selected = new ArrayList<>(standhouderRepo.findAllById(form.getStandhouderIds()));
        f.setStandhouders(selected);

        festivalRepo.save(f);
        return "redirect:/admin/festivals";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("festival", new FestivalForm());
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("regions", null);
        model.addAttribute("locations", locationRepo.findAll());
        model.addAttribute("standhouders", standhouderRepo.findAll());
        return "admin/festival-form"; // <-- juiste pad/naam
    }

    @PostMapping
    public String create(@ModelAttribute("festival") FestivalForm form) {
        Festival f = new Festival();
        f.setName(form.getName());
        f.setStartDateTime(form.getStartDateTime());
        f.setAvailableTickets(form.getAvailableTickets());
        f.setTicketPrice(form.getTicketPrice());
        f.setCategory(categoryRepo.findById(form.getCategory()).orElseThrow());
        f.setLocation(locationRepo.findById(form.getLocation()).orElseThrow());
        f.setStandhouders(new ArrayList<>(standhouderRepo.findAllById(form.getStandhouderIds())));
        festivalRepo.save(f);
        return "redirect:/admin/festivals";
    }
}
