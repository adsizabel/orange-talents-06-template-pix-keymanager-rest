package br.com.zup.ot6.izabel.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NovaChavePixResponse(
    @JsonProperty("pixId")
    val pixId: String
)
