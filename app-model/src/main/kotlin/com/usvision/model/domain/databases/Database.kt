package com.usvision.model.domain.databases

import com.usvision.model.visitor.Visitable
import kotlinx.serialization.Serializable

@Serializable
sealed interface Database : Visitable { //É extensível, mas não é flexível.
    //Requer pouco esforço para conseguir fazer outros contextos.
    val id: String?
    val description: String
}