package ygmd.kmpquiz.domain.result

interface GenericResult {
    data object Success : GenericResult
    sealed interface Failure : GenericResult
}