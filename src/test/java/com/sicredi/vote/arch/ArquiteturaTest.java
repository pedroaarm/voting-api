package com.sicredi.vote.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.sicredi.vote", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquiteturaTest {

    @ArchTest
    static final ArchRule dominioNaoDependeDeFramework = noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..", "jakarta.persistence..", "com.fasterxml..");

    @ArchTest
    static final ArchRule aplicacaoNaoDependeDeAdapters = noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat().resideInAPackage("..adapters..");

    @ArchTest
    static final ArchRule aplicacaoNaoDependeDeJpaOuWeb = noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "jakarta.persistence..", "org.springframework.web..");

    @ArchTest
    static final ArchRule webNaoDependeDePersistence = noClasses()
        .that().resideInAPackage("..adapters.web..")
        .should().dependOnClassesThat().resideInAPackage("..adapters.persistence..");

    @ArchTest
    static final ArchRule camadas = layeredArchitecture().consideringOnlyDependenciesInLayers()
        .layer("Domain").definedBy("..domain..")
        .layer("Application").definedBy("..application..")
        .layer("Adapters").definedBy("..adapters..")
        .layer("Config").definedBy("..config..")
        .whereLayer("Adapters").mayNotBeAccessedByAnyLayer()
        .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapters", "Config");
}
