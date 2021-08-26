package br.com.zup.ot6.izabel.dto

import br.com.zup.ot6.izabel.TipoChavePix
import br.com.zup.ot6.izabel.TipoConta
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RegistraChavePixRequestDTO(
    @JsonProperty("tipoChave")
    @field:NotBlank
    val tipoChave: TipoChavePix,
    @JsonProperty("valorChave")
    @field:NotBlank
    val valorChave: String,
    @JsonProperty("tipoConta")
    @field:NotBlank
    val tipoConta: TipoConta
    )

