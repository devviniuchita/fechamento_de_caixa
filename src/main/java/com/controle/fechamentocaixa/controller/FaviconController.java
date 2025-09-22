package com.controle.fechamentocaixa.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Responde /favicon.ico para evitar 404/401. O ícone real é fornecido via link
 * data URL no index.html.
 */
@RestController
public class FaviconController {

  @GetMapping(value = "/favicon.ico")
  public ResponseEntity<Void> favicon() {
    return ResponseEntity
        .noContent()
        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
        .build();
  }
}
