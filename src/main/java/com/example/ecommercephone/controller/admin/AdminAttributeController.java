package com.example.ecommercephone.controller.admin;

import com.example.ecommercephone.entity.Attribute;
import com.example.ecommercephone.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/attributes")
public class AdminAttributeController {
    
    @Autowired
    private AttributeService attributeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("attributes", attributeService.findAllAttributes());
        return "admin/attribute/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("attribute", new Attribute());
        model.addAttribute("isEdit", false);
        return "admin/attribute/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Attribute> attribute = attributeService.findAttributeById(id);
        if (attribute.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Attribute not found");
            return "redirect:/admin/attributes";
        }
        model.addAttribute("attribute", attribute.get());
        model.addAttribute("isEdit", true);
        return "admin/attribute/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Attribute attribute, RedirectAttributes redirectAttributes) {
        try {
            attributeService.createAttribute(attribute);
            redirectAttributes.addFlashAttribute("success", "Attribute created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating attribute: " + e.getMessage());
            return "redirect:/admin/attributes/new";
        }
        return "redirect:/admin/attributes";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id, @ModelAttribute Attribute attribute, RedirectAttributes redirectAttributes) {
        try {
            attribute.setId(id);
            attributeService.updateAttribute(attribute);
            redirectAttributes.addFlashAttribute("success", "Attribute updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating attribute: " + e.getMessage());
            return "redirect:/admin/attributes/" + id + "/edit";
        }
        return "redirect:/admin/attributes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            attributeService.deleteAttribute(id);
            redirectAttributes.addFlashAttribute("success", "Attribute deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting attribute: " + e.getMessage());
        }
        return "redirect:/admin/attributes";
    }
}

