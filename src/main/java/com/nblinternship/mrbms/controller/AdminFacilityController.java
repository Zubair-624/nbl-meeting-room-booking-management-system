package com.nblinternship.mrbms.controller;

import com.nblinternship.mrbms.entity.Facility;
import com.nblinternship.mrbms.service.FacilityService;
import com.nblinternship.mrbms.service.MeetingRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/facilities")
public class AdminFacilityController {

    private final FacilityService facilityService;
    private final MeetingRoomService meetingRoomService;

    public AdminFacilityController(FacilityService facilityService, MeetingRoomService meetingRoomService) {
        this.facilityService = facilityService;
        this.meetingRoomService = meetingRoomService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("facilities", facilityService.getAllFacilities());
        model.addAttribute("facility", new Facility());
        return "admin-facilities-list";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("facility") Facility facility, RedirectAttributes redirectAttributes) {
        facilityService.saveFacility(facility);
        redirectAttributes.addFlashAttribute("successMessage", "Facility saved.");
        return "redirect:/admin/facilities";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        facilityService.deleteFacility(id);
        redirectAttributes.addFlashAttribute("successMessage", "Facility deleted.");
        return "redirect:/admin/facilities";
    }

    @GetMapping("/assign/{roomId}")
    public String showAssignForm(@PathVariable("roomId") Integer roomId, Model model,
                                 RedirectAttributes redirectAttributes) {
        return meetingRoomService.getRoomById(roomId)
                .map(room -> {
                    model.addAttribute("room", room);
                    model.addAttribute("allFacilities", facilityService.getAllFacilities());
                    model.addAttribute("assignedFacilities", facilityService.getFacilitiesByRoomId(roomId));
                    return "admin-facility-assign";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Room not found.");
                    return "redirect:/admin/rooms";
                });
    }

    @PostMapping("/assign/{roomId}")
    public String saveAssignment(@PathVariable("roomId") Integer roomId,
                                 @RequestParam(value = "facilityIds", required = false) List<Integer> facilityIds,
                                 RedirectAttributes redirectAttributes) {
        facilityService.assignFacilitiesToRoom(roomId, facilityIds);
        redirectAttributes.addFlashAttribute("successMessage", "Facilities updated for this room.");
        return "redirect:/admin/rooms";
    }
}