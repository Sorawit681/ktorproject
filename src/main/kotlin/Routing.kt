package com.tracker

import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ==== Data Models ====
@Serializable
data class Transaction(
    val id: String,
    val description: String,
    val amount: Double,
    val type: String, // "income" or "expense"
    val date: String, // format: yyyy-MM-dd
    val categoryId: String
)

@Serializable
data class Category(
    val id: String,
    val name: String
)

@Serializable
data class MonthlyReportItem(
    val category: String,
    val total: Double
)

// ==== In-Memory Storage ====
val transactions = mutableListOf<Transaction>()
val categories = mutableListOf<Category>()

fun Application.configureRouting() {
    routing {
        // Root
        get("/") {
            call.respondText("Expense Tracker API")
        }

        // ===== Category CRUD =====
        get("/categories") {
            call.respond(categories)
        }

        post("/categories") {
            val category = call.receive<Category>()
            if (categories.any { it.id == category.id }) {
                call.respond(mapOf("error" to "Category ID already exist"))
            } else {
                categories.add(category)
                call.respond(mapOf("status" to "Category added"))
            }
        }

        put("/categories/{id}") {
            val id = call.parameters["id"]
            val updated = call.receive<Category>()
            val index = categories.indexOfFirst { it.id == id }
            if (index != -1) {
                categories[index] = updated
                call.respond(mapOf("status" to "Category updated"))
            } else {
                call.respond(mapOf("error" to "Category not found"))
            }
        }

        delete("/categories/{id}") {
            val id = call.parameters["id"]
            categories.removeIf { it.id == id }
            call.respond(mapOf("status" to "Category deleted"))
        }

        // ===== Transaction CRUD =====
        get("/transactions") {
            call.respond(transactions)
        }

        post("/transactions") {
            val transaction = call.receive<Transaction>()
            if (transactions.any { it.id == transaction.id }) {
                call.respond(mapOf("error!!" to "Transaction ID already exists"))
            } else {
                transactions.add(transaction)
                call.respond(mapOf("status" to "Transaction added"))
            }
        }

        put("/transactions/{id}") {
            val id = call.parameters["id"]
            val updated = call.receive<Transaction>()
            val index = transactions.indexOfFirst { it.id == id }
            if (index != -1) {
                transactions[index] = updated
                call.respond(mapOf("status" to "Transaction updated"))
            } else {
                call.respond(mapOf("error!!" to "Transaction not found"))
            }
        }

        delete("/transactions/{id}") {
            val id = call.parameters["id"]
            transactions.removeIf { it.id == id }
            call.respond(mapOf("status" to "Transaction deleted"))
        }

        // ===== Monthly Report =====
        get("/reports/monthly") {
            val year = call.request.queryParameters["year"]?.toIntOrNull()
            val month = call.request.queryParameters["month"]?.toIntOrNull()

            if (year == null || month == null) {
                call.respond(mapOf("error!!" to "Missing year and month query parameter"))
                return@get
            }

            //println("$year , $month")

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val filtered = transactions.filter {
                val txDate = LocalDate.parse(it.date, formatter)
                txDate.year == year && txDate.monthValue == month && it.type == "expense"
            }

            val grouped = filtered.groupBy { it.categoryId }
                .map { (catId, txs) ->
                    val catName = categories.find { it.id == catId }?.name ?: "Unknown this name"
                    MonthlyReportItem(category = catName, total = txs.sumOf { it.amount })
                }

            call.respond(grouped)
        }
    }
}
