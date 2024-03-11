package com.usvision.persistence.repositorybuilder

interface DBConnectionProvider<T> : DBConnectionBuilder {
    fun getConnection(): T
}