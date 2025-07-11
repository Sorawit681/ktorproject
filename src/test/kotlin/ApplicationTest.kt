package com.tracker

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            //configureSerialization()
            configureRouting()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Expense Tracker API", response.bodyAsText())
    }

    @Test
    fun testAddCategory() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }
        val category = Category(id = "cat123", name = "food")
        val response = client.post("/categories") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(category))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Category added"))
    }

    @Test
    fun testAddTransactionAndMonthlyReport() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }

        // Add category
        client.post("/categories") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Category(id = "cat123", name = "food")))
        }

        // Add transaction (July 2025)
        client.post("/transactions") {
            contentType(ContentType.Application.Json)
            setBody(
                Json.encodeToString(
                    Transaction(
                        id = "tx1",
                        description = "Lunch",
                        amount = 150.0,
                        type = "expense",
                        date = "2025-07-11",
                        categoryId = "cat1"
                    )
                )
            )
        }


        val reportResponse = client.get("/reports/monthly?year=2025&month=7")
        assertEquals(HttpStatusCode.OK, reportResponse.status)
        val body = reportResponse.bodyAsText()
        assertTrue(body.contains("food"))
        assertTrue(body.contains("150.0"))
    }
}
