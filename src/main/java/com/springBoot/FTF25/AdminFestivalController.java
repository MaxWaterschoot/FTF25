package com.springBoot.FTF25;

import domain.Festival;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/festivals")
public class AdminFestivalController {

    private final FestivalService festivalService;
    private final CategoryService categoryService;
    private final RegionService regionService;
    private final LocationService locationService;
    private final StandhouderService standhouderService;

    @GetMapping
    public String list(Model model) {
        List<Festival> all = festivalService.findAllOrderByDate();
        model.addAttribute("festivals", all);
        return "admin/festivals-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("festival", new Festival());
        fillLists(model);
        return "admin/festival-form";
    }

    @PostMapping
    public String create(@ModelAttribute Festival festival, RedirectAttributes ra) {
        try {
            festivalService.create(festival);
            ra.addAttribute("success", "Festival aangemaakt.");
            return "redirect:/admin/festivals";
        } catch (Exception e) {
            ra.addAttribute("error", e.getMessage());
            return "redirect:/admin/festivals/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Festival f = festivalService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Festival not found"));
        model.addAttribute("festival", f);
        fillLists(model);
        return "admin/festival-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Festival festival, RedirectAttributes ra) {
        try {
            festivalService.update(id, festival);
            ra.addAttribute("success", "Festival bijgewerkt.");
        } catch (Exception e) {
            ra.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/festivals";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            festivalService.delete(id);
            ra.addAttribute("success", "Festival verwijderd.");
        } catch (Exception e) {
            ra.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/festivals";
    }

    private void fillLists(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("regions", regionService.findAll());
        model.addAttribute("locations", locationService.findAll());
        model.addAttribute("standhouders", standhouderService.findAll());
    }
}
