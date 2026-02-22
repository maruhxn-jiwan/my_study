package com.aswemake.my_study

import org.springframework.expression.ExpressionParser
import org.springframework.expression.ParserContext
import org.springframework.expression.common.TemplateParserContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

@Component
class MyCacheKeyGenerator {
    private val parser: ExpressionParser = SpelExpressionParser()
    private val templateParserContext: ParserContext = TemplateParserContext()

    /**
     * @return {cacheStrategy}:{cacheName}:{key}
     */
    fun genKey(
        parameterNames: Array<String>,
        args: Array<Any>,
        keySpel: String,
        cacheStrategy: CacheStrategy,
        cacheName: String
    ): String {
        val context = StandardEvaluationContext()

        for (i in parameterNames.indices) {
            context.setVariable(parameterNames[i], args[i])
        }

        val exp = parser.parseExpression(keySpel, templateParserContext)
        val key = exp.getValue(context, String::class.java)

        return "${cacheStrategy}:${cacheName}:${key}"
    }
}

