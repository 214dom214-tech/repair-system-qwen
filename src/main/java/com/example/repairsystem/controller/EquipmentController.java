package com.example.repairsystem.controller;

import com.example.repairsystem.model.Equipment;
import com.example.repairsystem.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/equipment", produces = "application/json; charset=UTF-8")
public class EquipmentController {

    @Autowired
    private EquipmentRepository equipmentRepository;

    // CREATE — создать оборудование
    @PostMapping
    public List<Equipment> createMultipleEquipment(@RequestBody List<Equipment> equipmentList) {
        return equipmentRepository.saveAll(equipmentList);
    }

    // READ — получить все оборудование
    @GetMapping
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    // READ — получить по ID
    @GetMapping("/{id}")
    public Equipment getEquipmentById(@PathVariable Long id) {
        return equipmentRepository.findById(id).orElse(null);
    }

    // UPDATE — обновить оборудование
    @PutMapping("/{id}")
    public Equipment updateEquipment(@PathVariable Long id, @RequestBody Equipment updated) {
        Equipment equipment = equipmentRepository.findById(id).orElse(null);
        if (equipment == null) {
            return null;
        }

        equipment.setName(updated.getName());
        equipment.setInventoryNumber(updated.getInventoryNumber());
        equipment.setResponsiblePerson(updated.getResponsiblePerson());
        equipment.setNote(updated.getNote());

        return equipmentRepository.save(equipment);
    }

    // DELETE — удалить оборудование
    @DeleteMapping("/{id}")
    public void deleteEquipment(@PathVariable Long id) {
        equipmentRepository.deleteById(id);
    }
}