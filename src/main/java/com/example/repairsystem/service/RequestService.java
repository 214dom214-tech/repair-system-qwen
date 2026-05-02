package com.example.repairsystem.service;

import com.example.repairsystem.model.*;
import com.example.repairsystem.repository.EquipmentRepository;
import com.example.repairsystem.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {

    @Autowired private RequestRepository requestRepository;
    @Autowired private EquipmentRepository equipmentRepository;

    public List<Request> getAll() {
        return requestRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Request> getByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    public List<Request> getByPriority(RequestPriority priority) {
        return requestRepository.findByPriority(priority);
    }

    public Request getById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Заявка с id=" + id + " не найдена"));
    }

    public Request create(Request request) {
        resolveEquipment(request, request.getEquipment());
        if (request.getStatus() == null)   request.setStatus(RequestStatus.NEW);
        if (request.getPriority() == null) request.setPriority(RequestPriority.NORMAL);
        request.setCreatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    /** Принять в работу: NEW → IN_PROGRESS */
    public Request accept(Long id) {
        Request req = getById(id);
        requireStatus(req, RequestStatus.NEW, "Принять в работу");
        req.setStatus(RequestStatus.IN_PROGRESS);
        return requestRepository.save(req);
    }

    /** Закрыть после ремонта: IN_PROGRESS → CLOSED */
    public Request close(Long id) {
        Request req = getById(id);
        requireStatus(req, RequestStatus.IN_PROGRESS, "Закрыть");
        req.setStatus(RequestStatus.CLOSED);
        return requestRepository.save(req);
    }

    /** Подтвердить ремонт: CLOSED → CONFIRMED */
    public Request confirm(Long id) {
        Request req = getById(id);
        requireStatus(req, RequestStatus.CLOSED, "Подтвердить");
        req.setStatus(RequestStatus.CONFIRMED);
        return requestRepository.save(req);
    }

    public Request update(Long id, Request updated) {
        Request existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setPriority(updated.getPriority());
        resolveEquipment(existing, updated.getEquipment());
        return requestRepository.save(existing);
    }

    public void delete(Long id) {
        if (!requestRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заявка с id=" + id + " не найдена");
        requestRepository.deleteById(id);
    }

    // ---- helpers ----

    private void resolveEquipment(Request target, Equipment ref) {
        if (ref != null && ref.getId() != null) {
            Equipment eq = equipmentRepository.findById(ref.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Оборудование с id=" + ref.getId() + " не найдено"));
            target.setEquipment(eq);
        } else {
            target.setEquipment(null);
        }
    }

    private void requireStatus(Request req, RequestStatus expected, String action) {
        if (req.getStatus() != expected)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    action + " можно только из статуса " + expected + " (текущий: " + req.getStatus() + ")");
    }
}
