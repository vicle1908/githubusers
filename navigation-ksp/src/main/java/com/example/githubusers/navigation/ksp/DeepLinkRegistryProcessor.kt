package com.example.githubusers.navigation.ksp

import com.example.githubusers.navigation.annotations.OwnsDeepLinks
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

/**
 * KSP processor for generating deep link registry from @OwnsDeepLinks annotations.
 */
class DeepLinkRegistryProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(OwnsDeepLinks::class.qualifiedName!!)
        val ret = symbols.filter { !it.validate() }.toList()

        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(DeepLinkVisitor(), Unit) }

        return ret
    }

    private inner class DeepLinkVisitor : com.google.devtools.ksp.symbol.KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            logger.info("Processing deep link class: ${classDeclaration.qualifiedName?.asString()}")
            // For now, just log the processing - actual code generation can be added later
        }
    }
}

/**
 * Provider for the DeepLinkRegistryProcessor.
 */
class DeepLinkRegistryProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DeepLinkRegistryProcessor(environment.codeGenerator, environment.logger)
    }
}
