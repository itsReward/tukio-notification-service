package com.tukio.notificationservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.StringTemplateResolver

@Configuration
class EmailConfig {

    /**
     * Configure Thymeleaf for processing email templates
     */
    @Bean
    fun emailTemplateEngine(): TemplateEngine {
        val templateEngine = TemplateEngine()

        val templateResolver = StringTemplateResolver()
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.setCacheable(false)

        templateEngine.setTemplateResolver(templateResolver)

        return templateEngine
    }
}