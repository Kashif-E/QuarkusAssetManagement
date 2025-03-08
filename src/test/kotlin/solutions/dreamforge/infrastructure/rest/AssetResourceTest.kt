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
