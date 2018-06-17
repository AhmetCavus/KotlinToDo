package com.ams.cavus.todo.helper

import com.microsoft.windowsazure.mobileservices.table.query.Query
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations

class QueryBuilder {

    companion object {
        fun buildFetch(userId: String): Query {
            val query = QueryOperations.field("userId")
            return query.eq(userId)
        }
    }

}