package com.example.repairsystem.service;

import com.example.repairsystem.model.Equipment;
import com.example.repairsystem.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    public List<Equipment> getAll() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> search(String query) {
        return equipmentRepository.findByNameContainingIgnoreCase(query);
    }

    public Equipment getById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Оборудование с id=" + id + " не найдено"));
    }

    public List<Equipment> createBatch(List<Equipment> list) {
        // Проверяем уникальность инвентарного номера перед сохранением
        for (Equipment e : list) {
            equipmentRepository.findByInventoryNumber(e.getInventoryNumber())
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Инвентарный номер '" + e.getInventoryNumber() + "' уже занят");
                    });
        }
        return equipmentRepository.saveAll(list);
    }

    public Equipment update(Long id, Equipment updated) {
        Equipment existing = getById(id);

        // Проверяем уникальность инвентарного номера (исключая текущую запись)
        equipmentRepository.findByInventoryNumber(updated.getInventoryNumber())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(conflict -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Инвентарный номер '" + updated.getInventoryNumber() + "' уже занят");
                });

        existing.setName(updated.getName());
        existing.setInventoryNumber(updated.getInventoryNumber());
        existing.setResponsiblePerson(updated.getResponsiblePerson());
        existing.setNote(updated.getNote());

        return equipmentRepository.save(existing);
    }

    public void delete(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Оборудование с id=" + id + " не найдено");
        }
        equipmentRepository.deleteById(id);
    }
}
