package com.arffy.server.domian.delivery.dto

data class DeliveryTrackerDto(
    val from: From,
    val to: To,
    val state: State,
    val progresses: List<Progress>,
    val carrier: Carrier,
) {
    class From(
        val name: String,
        val time: String,
    )

    class To(
        val name: String,
        val time: String,
    )

    class State(
        val id: String,
        val text: String,
    )

    class Progress(
        val time: String,
        val location: Location,
        val status: Status,
        val description: String? = null
    ) {
        class Location(
            val name: String,
        )

        class Status(
            val id: String,
            val text: String,
        )
    }

    class Carrier(
        val id: String,
        val name: String,
        val tel: String,
    )
}