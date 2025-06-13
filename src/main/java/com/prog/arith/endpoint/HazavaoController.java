package com.prog.arith.endpoint;

import com.prog.arith.service.OpenAiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/hazavao")
public class HazavaoController {
  private final OpenAiService openAiService;

  @GetMapping
  public ResponseEntity<String> hazavao(@RequestParam("teny") String teny) {
    String definition = openAiService.getMalagasyDefinition(teny);
    return ResponseEntity.ok(definition);
  }
}
