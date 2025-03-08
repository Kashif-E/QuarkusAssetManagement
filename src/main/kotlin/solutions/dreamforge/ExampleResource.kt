package solutions.dreamforge

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

/**
 * Default resource endpoint for the Enterprise Asset Management System.
 * This simple endpoint serves as a health check and welcome message for the application.
 *
 * In a production environment, this would typically be expanded to include:
 * - A proper health check endpoint following Microprofile Health specifications
 * - A welcome page or redirection to the API documentation
 * - Version information and system status
 *
 * The @Path annotation from JAX-RS (javax.ws.rs package) defines the base path for this resource.
 */
@Path("/hello")
class ExampleResource {

    /**
     * Simple endpoint that returns a welcome message in plain text.
     * This can be used as a basic health check to verify the application is running.
     *
     * HTTP Method: GET
     * Path: /hello
     * Response: 200 OK with text message
     *
     * @return A welcome message string
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "Hello from Enterprise Asset Management System"
}
