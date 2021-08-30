package br.com.zup.ot6.izabel.dto

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


    fun Timestamp.paraLocalDateTime(): LocalDateTime {
        return Instant.ofEpochSecond(
            this.seconds,
            this.nanos.toLong()
        )
            .atZone(ZoneId.of("UTC"))
            .toLocalDateTime()
    }

