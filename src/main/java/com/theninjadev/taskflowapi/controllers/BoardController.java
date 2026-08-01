package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.board.BoardDto;
import com.theninjadev.taskflowapi.dtos.board.CreateBoardRequest;
import com.theninjadev.taskflowapi.services.BoardService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        var currentUserId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var boardDto = boardService.createBoard(request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(boardDto);
    }


}
