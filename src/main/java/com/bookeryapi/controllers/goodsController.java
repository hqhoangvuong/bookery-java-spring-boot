package com.bookeryapi.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookeryapi.dto.Goods;
import com.bookeryapi.services.GoodsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "GoodsController", description = "Operations with goods")
public class goodsController {
    private final GoodsService goodsService;

    @Operation(summary = "Creation of goods")
    @PostMapping("/create")
    public Goods create(@RequestBody Goods goods) {
        return goodsService.create(goods);
    }

    @Operation(summary = "Get goods by id")
    @GetMapping("/{id}")
    public Goods getById(@PathVariable UUID id) {
        return goodsService.getById(id);
    }

    @Operation(summary = "Update goods")
    @PutMapping("/update")
    public Goods update(@RequestBody Goods goods) {
        return goodsService.update(goods);
    }

    @Operation(summary = "Delete by id")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        goodsService.delete(id);
    }
}
