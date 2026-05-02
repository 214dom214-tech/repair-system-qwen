package com.example.repairsystem.controller;

import com.example.repairsystem.model.Request;
import com.example.repairsystem.model.Equipment;
import com.example.repairsystem.repository.RequestRepository;
import com.example.repairsystem.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    // POST-запрос для создания новой заявки
    @PostMapping
    public Request createRequest(@RequestBody Request request) {
        // Проверяем, есть ли оборудование с указанным ID
        Equipment equipment = equipmentRepository.findById(request.getEquipment().getId()).orElse(null);

        // Если оборудование найдено, привязываем его к заявке
        if (equipment != null) {
            request.setEquipment(equipment);
        }

        // Устанавливаем дату создания заявки
        request.setCreatedAt(LocalDateTime.now());

        // Сохраняем заявку в базе
        return requestRepository.save(request);
    }

    // GET-запрос: получить все заявки
    @GetMapping
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    // GET-запрос: получить заявку по ID
    @GetMapping("/{id}")
    public Request getRequestById(@PathVariable Long id) {
        return requestRepository.findById(id).orElse(null);
    }

    // PUT-запрос: обновить заявку
    @PutMapping("/{id}")
    public Request updateRequest(@PathVariable Long id, @RequestBody Request updatedRequest) {
        Request request = requestRepository.findById(id).orElse(null);
        if (request == null) {
            return null;
        }
        request.setTitle(updatedRequest.getTitle());
        request.setDescription(updatedRequest.getDescription());
        request.setStatus(updatedRequest.getStatus());
        return requestRepository.save(request);
    }

    // DELETE-запрос: удалить заявку
    @DeleteMapping("/{id}")
    public void deleteRequest(@PathVariable Long id) {
        requestRepository.deleteById(id);
    }
}