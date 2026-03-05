package com.example.ecommercephone.controller.admin;

import com.example.ecommercephone.entity.AttributeValue;
import com.example.ecommercephone.service.AttributeService;
import com.example.ecommercephone.service.AttributeValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/attribute-values")
public class AdminAttributeValueController {
    
    @Autowired
    private AttributeService attributeService;

    @Autowired
    private AttributeValueService attributeValueService;

    @GetMapping
    public String list(@RequestParam(required = false) Integer attributeId, Model model) {
        if (attributeId != null) {
            model.addAttribute("attributeValues", attributeValueService.findValuesByAttributeId(attributeId));
            model.addAttribute("selectedAttributeId", attributeId);
            attributeService.findAttributeById(attributeId).ifPresent(attr -> 
                model.addAttribute("selectedAttributeName", attr.getName())
            );
        } else {
            model.addAttribute("attributeValues", attributeService.findAllAttributes());
        }
        model.addAttribute("attributes", attributeService.findAllAttributes());
        return "admin/attribute-value/list";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Integer attributeId, Model model) {
        var attributeValue = new AttributeValue();
        if (attributeId != null) {
            attributeService.findAttributeById(attributeId).ifPresent(attributeValue::setAttribute);
        }
        model.addAttribute("attributeValue", attributeValue);
        model.addAttribute("attributes", attributeService.findAllAttributes());
        model.addAttribute("isEdit", false);
        return "admin/attribute-value/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        var attributeValue = attributeValueService.findValueById(id);
        if (attributeValue.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Attribute value not found");
            return "redirect:/admin/attribute-values";
        }
        model.addAttribute("attributeValue", attributeValue.get());
        model.addAttribute("attributes", attributeService.findAllAttributes());
        model.addAttribute("isEdit", true);
        return "admin/attribute-value/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute AttributeValue attributeValue, RedirectAttributes redirectAttributes) {
        try {
            attributeValueService.createAttributeValue(attributeValue);
            redirectAttributes.addFlashAttribute("success", "Attribute value created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating attribute value: " + e.getMessage());
            return "redirect:/admin/attribute-values/new" +
                   (attributeValue.getAttribute() != null ? "?attributeId=" + attributeValue.getAttribute().getId() : "");
        }
        return "redirect:/admin/attribute-values" + 
               (attributeValue.getAttribute() != null ? "?attributeId=" + attributeValue.getAttribute().getId() : "");
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id, @ModelAttribute AttributeValue attributeValue, RedirectAttributes redirectAttributes) {
        try {
            attributeValue.setId(id);
            attributeValueService.updateAttributeValue(attributeValue);
            redirectAttributes.addFlashAttribute("success", "Attribute value updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating attribute value: " + e.getMessage());
            return "redirect:/admin/attribute-values/" + id + "/edit";
        }
        return "redirect:/admin/attribute-values" + 
               (attributeValue.getAttribute() != null ? "?attributeId=" + attributeValue.getAttribute().getId() : "");
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            var attributeValue = attributeValueService.findValueById(id);
            Integer attributeId = attributeValue.map(av -> av.getAttribute().getId()).orElse(null);
            attributeValueService.deleteAttributeValue(id);
            redirectAttributes.addFlashAttribute("success", "Attribute value deleted successfully");
            return "redirect:/admin/attribute-values" + (attributeId != null ? "?attributeId=" + attributeId : "");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting attribute value: " + e.getMessage());
            return "redirect:/admin/attribute-values";
        }
    }
}

