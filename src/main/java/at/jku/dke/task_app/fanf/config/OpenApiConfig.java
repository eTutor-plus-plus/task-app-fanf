package at.jku.dke.task_app.fanf.config;

import at.jku.dke.etutor.task_app.config.BaseOpenApiConfig;
import org.springframework.context.annotation.Configuration;

/**
 * The application OpenAPI configuration.
 */
@Configuration
public class OpenApiConfig extends BaseOpenApiConfig {
    /**
     * Creates a new instance of class {@link OpenApiConfig}.
     */
    public OpenApiConfig() {
        super("eTutor - FANF API", "API for tasks of type <code>Fanf</code>", OpenApiConfig.class.getPackage().getImplementationVersion());
    }
}
