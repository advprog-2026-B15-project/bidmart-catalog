package id.ac.ui.cs.advprog.bidmartcatalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        if (!uploadPath.endsWith(java.io.File.separator)) {
            uploadPath += java.io.File.separator;
        }

        // Uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);

        // Next.js Static Files
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);

                        // Handle SPA routing: if resource doesn't exist or doesn't have an extension, serve index.html
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        } else if (!resourcePath.contains(".")) {
                            // Support Next.js static export paths (e.g., /login -> /login.html)
                            Resource htmlResource = location.createRelative(resourcePath + ".html");
                            if (htmlResource.exists() && htmlResource.isReadable()) {
                                return htmlResource;
                            }
                            return new ClassPathResource("/static/index.html");
                        }
                        return null;
                    }
                });
    }
}