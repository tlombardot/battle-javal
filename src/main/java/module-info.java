module school.coda.darill_thomas_louis.bataillejavale {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens school.coda.darill_thomas_louis.bataillejavale.ui to javafx.fxml;
    exports school.coda.darill_thomas_louis.bataillejavale.ui;
}