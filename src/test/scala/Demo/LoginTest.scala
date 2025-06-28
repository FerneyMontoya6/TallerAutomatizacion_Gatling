package Demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Demo.Data._

class LoginTest extends Simulation {

  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val login = exec(http("Login Correcto")
    .post("users/login")
    .body(StringBody(s"""{"email": "$email", "password": "$password"}""")).asJson
    .check(status.is(200))
    .check(jsonPath("$.token").saveAs("authToken"))
  )

  val createContact = exec(session => {
      val emailGen = s"gatling_${System.currentTimeMillis}@mail.com"
      session.set("contactEmail", emailGen)
    })
    .exec(http("Crear Contacto")
      .post("contacts")
      .header("Authorization", "Bearer ${authToken}")
      .body(StringBody(
        """{
          "firstName": "Christian",
          "lastName": "Gomez",
          "birthdate": "1970-01-01",
          "email": "${contactEmail}",
          "phone": "8005555555"
        }""")).asJson
      .check(status.is(201))
    )

  val invalidLogin = scenario("Login Inválido")
    .exec(http("Login con credenciales incorrectas")
      .post("users/login")
      .body(StringBody("""{"email": "wrong@mail.com", "password": "wrong"}""")).asJson
      .check(status.is(401))
      .check(jsonPath("$.error").is("Incorrect email or password"))
    )

  val scn = scenario("Login + Crear Contacto")
    .exec(login)
    .pause(1)
    .exec(createContact)

  setUp(
    scn.inject(rampUsersPerSec(5).to(15).during(30)),
    invalidLogin.inject(atOnceUsers(5)) // Testeos puntuales
  ).protocols(httpConf)
}
package Demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Demo.Data._

class LoginTest extends Simulation {

  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val login = exec(http("Login Correcto")
    .post("users/login")
    .body(StringBody(s"""{"email": "$email", "password": "$password"}""")).asJson
    .check(status.is(200))
    .check(jsonPath("$.token").saveAs("authToken"))
  )

  val createContact = exec(session => {
      val emailGen = s"gatling_${System.currentTimeMillis}@mail.com"
      session.set("contactEmail", emailGen)
    })
    .exec(http("Crear Contacto")
      .post("contacts")
      .header("Authorization", "Bearer ${authToken}")
      .body(StringBody(
        """{
          "firstName": "Christian",
          "lastName": "Gomez",
          "birthdate": "1970-01-01",
          "email": "${contactEmail}",
          "phone": "8005555555"
        }""")).asJson
      .check(status.is(201))
    )

  val invalidLogin = scenario("Login Inválido")
    .exec(http("Login con credenciales incorrectas")
      .post("users/login")
      .body(StringBody("""{"email": "wrong@mail.com", "password": "wrong"}""")).asJson
      .check(status.is(401))
      .check(jsonPath("$.error").is("Incorrect email or password"))
    )

  val scn = scenario("Login + Crear Contacto")
    .exec(login)
    .pause(1)
    .exec(createContact)

  setUp(
    scn.inject(rampUsersPerSec(5).to(15).during(30)),
    invalidLogin.inject(atOnceUsers(5)) 
  ).protocols(httpConf)
}
