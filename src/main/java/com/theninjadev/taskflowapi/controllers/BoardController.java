package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.board.BoardDto;
import com.theninjadev.taskflowapi.dtos.board.CreateBoardRequest;
import com.theninjadev.taskflowapi.services.BoardService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/boards")
@AllArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardDto> createBoard(
            @Valid @RequestBody CreateBoardRequest request
            ) {
        var currentUserId = getCurrentUserId();

        var boardDto = boardService.createBoard(request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> getBoard(
            @PathVariable(value = "id") UUID boardId
    ) {
        var currentUserId = getCurrentUserId();

        var boardDto = boardService.getBoard(boardId, currentUserId);

        return ResponseEntity.ok(boardDto);
    }

    @GetMapping
    public ResponseEntity<List<BoardDto>> listBoardsForUser() {
        var currentUserId = getCurrentUserId();

        var boardDtos = boardService.listBoardsForUser(currentUserId);

        return ResponseEntity.ok(boardDtos);
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
