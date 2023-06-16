package com.example.moneytracker.data.local

enum class RepoKey {
    TRANSACTION
}

open class MoneyTrackerRepository(
    private val database: MoneyTrackerDb,
    private val register: Set<MoneyTrackerRepository>
    ) {
    open val key: String = ""

    fun <T : MoneyTrackerRepository> get(): MoneyTrackerRepository? {
        return this.register.firstOrNull {
            it.key == this.key
        }
    }
}