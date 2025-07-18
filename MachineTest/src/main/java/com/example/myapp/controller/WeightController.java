package com.example.myapp.controller;

import com.example.myapp.Models.Weight;
import com.example.myapp.Repository.WeightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class WeightController {

    @Autowired
    private WeightRepository weightRepository;

    @GetMapping("/weight/add")
    public String showAddForm(Model model) {
        model.addAttribute("weight", new Weight());
        return "addWeight";
    }

    @PostMapping("/weight/add")
    public String addWeight(@ModelAttribute Weight weight, Principal principal, Model model) {
        String username = principal.getName();
        Optional<Weight> existing = weightRepository.findTodayEntry(username);
        if (existing.isPresent()) {
            model.addAttribute("error", "You have already added your weight today.");
            return "addWeight";
        }
        weight.setUsername(username);
        weight.setAddedTime(LocalDateTime.now());
        weightRepository.save(weight);
        model.addAttribute("success", "Weight entry added successfully.");
        return "redirect:/weight/list";
    }

    @GetMapping("/weight/list")
    public String listWeights(
            Model model,
            Principal principal,
            @RequestParam(defaultValue = "0") int page
    ) {
        String username = principal.getName();
        Page<Weight> weightPage = weightRepository.findByUsernameOrderByAddedTimeDesc(username, PageRequest.of(page, 5));
        model.addAttribute("weights", weightPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", weightPage.getTotalPages());
        return "weightList";
    }
    @GetMapping("/weight/edit/{id}")
    public String editWeight(@PathVariable Integer id, Model model) {
        Optional<Weight> weightOpt = weightRepository.findById(id);
        if (weightOpt.isPresent()) {
            model.addAttribute("weightObj", weightOpt.get());
            return "editWeight";
        } else {
            return "redirect:/weight/list";
        }
    }

    @PostMapping("/weight/edit/{id}")
    public String updateWeight(
            @PathVariable Integer id,
            @ModelAttribute("weightObj") Weight updatedWeight,
            Principal principal
    ) {
        Optional<Weight> optional = weightRepository.findById(id);
        if (optional.isPresent()) {
            Weight existing = optional.get();
            existing.setWeight(updatedWeight.getWeight());
            existing.setUsername(principal.getName()); // Keep the same user
            weightRepository.save(existing);
        }
        return "redirect:/weight/list";
    }

    @GetMapping("/weight/delete/{id}")
    public String deleteWeight(@PathVariable Integer id) {
        weightRepository.deleteById(id);
        return "redirect:/weight/list";
    }


    @GetMapping("/weight/loss")
    public String weightLoss(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Principal principal,
            Model model
    ) {
        // If startDate or endDate is missing, just show the form
        if (startDate == null || endDate == null) {
            return "weightLoss";  // return form view without processing
        }

        try {
            LocalDate startLocalDate = LocalDate.parse(startDate);
            LocalDate endLocalDate = LocalDate.parse(endDate);

            if (startLocalDate.isAfter(endLocalDate)) {
                model.addAttribute("message", "Start date must be before or equal to end date.");
                return "weightLoss";
            }

            LocalDateTime start = startLocalDate.atStartOfDay();
            LocalDateTime end = endLocalDate.atTime(LocalTime.MAX);
            String username = principal.getName();

            List<Weight> weights = weightRepository
                    .findByUsernameAndAddedTimeBetweenOrderByAddedTimeAsc(username, start, end);

            if (weights.size() >= 2) {
                double first = weights.get(0).getWeight();
                double last = weights.get(weights.size() - 1).getWeight();
                double loss = first - last;

                model.addAttribute("loss", loss);
                model.addAttribute("startWeight", first);
                model.addAttribute("endWeight", last);
            } else {
                model.addAttribute("message", "At least two weight entries are required in the selected date range.");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error processing dates. Please enter valid date format (yyyy-MM-dd).");
        }

        return "weightLoss";
    }

}
