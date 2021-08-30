package br.com.zup.ot6.izabel.dto

import br.com.zup.ot6.izabel.TipoChavePix
import br.com.zup.ot6.izabel.TipoConta
import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class ChavePixResponseDTO(
    val pixId: String,
    val clienteId: String,
    val tipoChave: TipoChavePix,
    val valorChave: String,
    val tipoConta: TipoConta,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val dataCriacao: LocalDateTime
)