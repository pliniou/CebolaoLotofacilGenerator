package com.example.cebolaolotofacilgenerator.model.common

// Definição do Enum OperacaoStatus
enum class OperacaoStatus {
    CARREGANDO,
    SUCESSO,
    ERRO,
    OCIOSO,
    INATIVO // Adicionado para compatibilidade com JogoViewModel, a ser confirmado
}