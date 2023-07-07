package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.domain.model.DataSource
import com.sortinghat.pattern_detector.domain.model.DatabaseAccessMode
import com.sortinghat.pattern_detector.domain.model.HttpVerb

fun httpVerbFromString(str: String) = when(str) {
    "GET"       -> HttpVerb.GET
    "POST"      -> HttpVerb.POST
    "PUT"       -> HttpVerb.PUT
    "PATCH"     -> HttpVerb.PATCH
    "DELETE"    -> HttpVerb.DELETE
    "OPTIONS"   -> HttpVerb.OPTIONS
    "HEAD"      -> HttpVerb.HEAD
    "CONNECT"   -> HttpVerb.CONNECT
    "TRACE"     -> HttpVerb.TRACE
    else        -> HttpVerb.GET
}

fun datasourceFromString(str: String) = when(str) {
    "relational"    -> DataSource.Relational
    "plain_text"    -> DataSource.PlainText
    "redis"         -> DataSource.Redis
    "mongodb"       -> DataSource.MongoDb
    "postgresql"    -> DataSource.PostgreSql
    "sqlserver"    -> DataSource.SqlServer
    "oracle"       -> DataSource.Oracle
    else            -> DataSource.Relational
}

fun accessModeFromString(str: String) = when(str) {
    "ro" -> DatabaseAccessMode.ReadOnly
    "wo" -> DatabaseAccessMode.WriteOnly
    else -> DatabaseAccessMode.ReadWrite
}