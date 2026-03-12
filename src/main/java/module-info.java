module school.coda.darill_thomas_louis.bataillejavale {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires annotations;
    requires java.desktop;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    opens school.coda.darill_thomas_louis.bataillejavale.core.model to com.fasterxml.jackson.databind;
    opens school.coda.darill_thomas_louis.bataillejavale.core.event to com.fasterxml.jackson.databind;

    opens school.coda.darill_thomas_louis.bataillejavale.ui to javafx.fxml;
    exports school.coda.darill_thomas_louis.bataillejavale.ui;
}