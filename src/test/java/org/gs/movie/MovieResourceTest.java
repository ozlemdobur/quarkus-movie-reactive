package org.gs.movie;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class MovieResourceTest {
    @Test
    void getAll() {
        Response response = given()
                .when()
                .get("/movies")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();
    }

    @Test
    void getById_success() {
        MovieEntity movie = given()
                .when()
                .get("/movies/1")
                .then().statusCode(200).extract().as(MovieEntity.class);
        Assertions.assertNotNull(movie);
        Assertions.assertNotNull(movie.getCountry());
    }

    @Test
    void getById_fail() {
        given()
                .when()
                .get("/movies/222")
                .then().statusCode(404);
    }
    @Test
    void getByTitle_success() {
        MovieEntity movie =given().when().get("/movies/title/Cherry")
                .then().statusCode(200).extract().as(MovieEntity.class);
        Assertions.assertNotNull(movie);
        Assertions.assertNotNull(movie.getTitle());
    }

    @Test
    void getByTitle_fail() {
        given().when().get("/movies/title/AAAA")
                .then().statusCode(404);
    }

    @Test
    void getByCountry_success() {
        MovieEntity[] movie = given().when().get("/movies/country/NL")
                .then().statusCode(200).extract().as(MovieEntity[].class);
        Assertions.assertNotNull(movie);
        Assertions.assertNotNull(movie[0].getCountry());
    }

    @Test
    void getByCountry_empty() {
        MovieEntity[] movies = given().when().get("/movies/country/AAAAAA")
                .then().statusCode(200).extract().as(MovieEntity[].class);
        Assertions.assertNotNull(movies);
        Assertions.assertEquals(0, movies.length);
    }

    @Test
    void delete_success() {
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle("Titanic");
        movieEntity.setDescription("Description");
        movieEntity.setCountry("NL");
        //insert
        MovieEntity entity = given().contentType(ContentType.JSON).body(movieEntity)
                .post("/movies").then().statusCode(201).extract().as(MovieEntity.class);
        //delete
        given().when().delete("/movies/"+entity.getId()).then().statusCode(204);
    }
    @Test
    void delete_fail(){
        given().when().delete("/movies/1111111").then().statusCode(404);
    }

    @Test
    void save_update_success() {
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle("Titanic");
        movieEntity.setDescription("Description");
        movieEntity.setCountry("NL");
        //insert
        MovieEntity entity = given().contentType(ContentType.JSON).body(movieEntity)
                .post("/movies").then().statusCode(201).extract().as(MovieEntity.class);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(entity.getCountry(),movieEntity.getCountry());

        //update
        entity.setTitle("update");
        MovieEntity updateEntity = given().contentType(ContentType.JSON).body(entity).when().put("/movies/"+entity.getId())
                .then().statusCode(200).extract().as(MovieEntity.class);
        Assertions.assertNotNull(updateEntity);

    }
    @Test
    void update_conflict() {
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setId(444L);
        given().contentType(ContentType.JSON).body(movieEntity).when().put("/movies/1").then().statusCode(409);
    }

    @Test
    void update_notFound() {
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setId(null);
        given().contentType(ContentType.JSON).body(movieEntity).when().put("/movies/"+movieEntity.getId()).then().statusCode(404);
    }
}