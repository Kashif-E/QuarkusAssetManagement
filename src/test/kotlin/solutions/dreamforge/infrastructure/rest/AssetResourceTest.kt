package solutions.dreamforge.infrastructure.rest

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import solutions.dreamforge.infrastructure.dto.CreateAssetRequest

@QuarkusTest
class AssetResourceTest {
    
    @Test
    fun `test get all assets`() {
        given()
            .`when`()
            .get("/api/assets")
            .then()
            .statusCode(200)
    }
    
    @Test
    fun `test create and get asset`() {
        // Create a new asset
        val assetId = given()
            .contentType(ContentType.JSON)
            .body(
                CreateAssetRequest(
                    name = "Test Asset",
                    description = "This is a test asset",
                    acquisitionDate = "2023-01-01",
                    acquisitionCost = 1000.0,
                    location = "Test Location",
                    category = "IT_EQUIPMENT"
                )
            )
            .`when`()
            .post("/api/assets")
            .then()
            .statusCode(201)
            .body("name", equalTo("Test Asset"))
            .body("description", equalTo("This is a test asset"))
            .body("acquisitionDate", equalTo("2023-01-01"))
            .body("acquisitionCost", equalTo(1000.0f))
            .body("currentValue", equalTo(1000.0f))
            .body("location", equalTo("Test Location"))
            .body("status", equalTo("ACTIVE"))
            .body("category", equalTo("IT_EQUIPMENT"))
            .body("id", notNullValue())
            .extract()
            .path<String>("id")
        
        // Get the created asset
        given()
            .`when`()
            .get("/api/assets/{id}", assetId)
            .then()
            .statusCode(200)
            .body("id", equalTo(assetId))
            .body("name", equalTo("Test Asset"))
    }
    
    @Test
    fun `test get asset by status`() {
        given()
            .`when`()
            .get("/api/assets/by-status/ACTIVE")
            .then()
            .statusCode(200)
    }
    
    @Test
    fun `test get asset by category`() {
        given()
            .`when`()
            .get("/api/assets/by-category/IT_EQUIPMENT")
            .then()
            .statusCode(200)
    }
    
    @Test
    fun `test get asset by location`() {
        given()
            .queryParam("location", "IT Department, Floor 3")
            .`when`()
            .get("/api/assets/by-location")
            .then()
            .statusCode(200)
    }
}
