package com.ddsi.utn.ba.ssr.controllers;

import com.ddsi.utn.ba.ssr.models.AuthResponseDTO;
import com.ddsi.utn.ba.ssr.services.MetamapaApiService;
import javax.print.attribute.standard.PresentationDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class LoginController {
  @Autowired
  private MetamapaApiService metamapaApiService;

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @PostMapping("/registrar")
  public String procesarRegistro(@RequestParam String username,
                                 @RequestParam String password, Model model) {
    try {
      Boolean pudoRegistrar = metamapaApiService.register(username, password);
      return "redirect:/login?registrado";
    }
    catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.CONFLICT) {
        return "redirect:/registro?userDuplicated";
      }
    }
    catch (Exception e) {
      return "redirect:/registro?error";
    }
    return "redirect:/registro?error";
  }

  @GetMapping("/registro")
  public String mostrarFormRegistro() {
    return "registro";
  }
}
