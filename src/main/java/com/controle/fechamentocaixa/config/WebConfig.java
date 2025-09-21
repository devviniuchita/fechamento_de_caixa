package com.controle.fechamentocaixa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Configuração para recursos estáticos e routing do frontend
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * Configura handlers para recursos estáticos
   */
  @Override
  public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
    // Serve arquivos estáticos da raiz do projeto
    registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/static/", "file:./")
        .setCachePeriod(0); // Desabilita cache em desenvolvimento

    // Handler específico para arquivos públicos
    registry.addResourceHandler("/public/**")
        .addResourceLocations("classpath:/static/public/", "file:./public/")
        .setCachePeriod(0);

    // Handler para CSS, JS, imagens
    registry.addResourceHandler("/css/**", "/js/**", "/images/**")
        .addResourceLocations("classpath:/static/css/", "classpath:/static/js/",
            "classpath:/static/images/", "file:./public/css/",
            "file:./public/js/", "file:./public/images/")
        .setCachePeriod(0);
  }

  /**
   * Configura view controllers para SPA routing
   */
  @Override
  public void addViewControllers(@NonNull ViewControllerRegistry registry) {
    // Mapeia rotas SPA para index.html
    registry.addViewController("/").setViewName("forward:/index.html");
    registry.addViewController("/dashboard").setViewName("forward:/index.html");
    registry.addViewController("/fechamentos").setViewName("forward:/index.html");
    registry.addViewController("/usuarios").setViewName("forward:/index.html");
    registry.addViewController("/login").setViewName("forward:/index.html");
  }
}
