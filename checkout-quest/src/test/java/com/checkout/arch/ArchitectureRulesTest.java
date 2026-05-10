package com.checkout.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Enforces CR-02, CR-05, CR-06 from the architecture description.
 */
class ArchitectureRulesTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.checkout");
    }

    // CR-06 / X3: pay  ment.* must not import order.domain or order.infrastructure
    @Test
    void payment_must_not_depend_on_order_domain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.checkout.payment..")
                .should().dependOnClassesThat()
                .resideInAPackage("com.checkout.order.domain..")
                .because("Payment BC must not import Order domain - communicate via integration events");
        rule.check(classes);
    }

    @Test
    void payment_must_not_depend_on_order_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.checkout.payment..")
                .should().dependOnClassesThat()
                .resideInAPackage("com.checkout.order.infrastructure..")
                .because("Payment BC must not import Order infrastructure - boundary violation");
        rule.check(classes);
    }

    // X1/X2: order.* must not import cart.infrastructure or cart.domain directly
    @Test
    void order_must_not_depend_on_cart_domain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.checkout.order..")
                .should().dependOnClassesThat()
                .resideInAPackage("com.checkout.cart.domain..")
                .because("Order must access Cart only via CartFacade interface (cart.application.api)");
        rule.check(classes);
    }

    @Test
    void order_must_not_depend_on_cart_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.checkout.order..")
                .should().dependOnClassesThat()
                .resideInAPackage("com.checkout.cart.infrastructure..")
                .because("Order must not reach into Cart infrastructure");
        rule.check(classes);
    }

    // X4: ledger.* must not import payment.infrastructure
    @Test
    void ledger_must_not_depend_on_payment_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.checkout.ledger..")
                .should().dependOnClassesThat()
                .resideInAPackage("com.checkout.payment.infrastructure..")
                .because("Ledger must not import Payment infrastructure - boundary violation");
        rule.check(classes);
    }

    // X5: *.domain.* must not use Spring annotations
    @Test
    void domain_must_not_use_spring_annotations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository")
                .because("Domain must be framework-agnostic (CR-05)");
        rule.check(classes);
    }

    // X6: *.domain.* must not use JPA annotations
    @Test
    void domain_must_not_use_jpa_annotations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("jakarta.persistence.Entity")
                .orShould().beAnnotatedWith("jakarta.persistence.Table")
                .because("Domain objects must not be JPA entities (CR-05)");
        rule.check(classes);
    }

    // X7: no module may import another module's .web.* package
    @Test
    void modules_must_not_import_each_others_web_layer() {
        ArchRule cartWebNotImported = noClasses()
                .that().resideOutsideOfPackage("com.checkout.cart.web..")
                .should().dependOnClassesThat().resideInAPackage("com.checkout.cart.web..")
                .because("cart.web is private to the Cart module");

        ArchRule orderWebNotImported = noClasses()
                .that().resideOutsideOfPackage("com.checkout.order.web..")
                .should().dependOnClassesThat().resideInAPackage("com.checkout.order.web..")
                .because("order.web is private to the Order module");

        ArchRule paymentWebNotImported = noClasses()
                .that().resideOutsideOfPackage("com.checkout.payment.web..")
                .should().dependOnClassesThat().resideInAPackage("com.checkout.payment.web..")
                .because("payment.web is private to the Payment module");

        ArchRule ledgerWebNotImported = noClasses()
                .that().resideOutsideOfPackage("com.checkout.ledger.web..")
                .should().dependOnClassesThat().resideInAPackage("com.checkout.ledger.web..")
                .because("ledger.web is private to the Ledger module");

        cartWebNotImported.check(classes);
        orderWebNotImported.check(classes);
        paymentWebNotImported.check(classes);
        ledgerWebNotImported.check(classes);
    }
}
