package ygmd.kmpquiz.infra.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine

actual fun platformEngine(): HttpClientEngine = OkHttpEngine(OkHttpConfig())