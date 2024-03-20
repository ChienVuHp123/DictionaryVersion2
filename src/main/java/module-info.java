module com.mypj.dictionaryversion2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.mypj.dictionaryversion2 to javafx.fxml;
    exports com.mypj.dictionaryversion2;
    opens com.mypj.dictionaryversion2.controller to javafx.fxml;
    exports com.mypj.dictionaryversion2.controller;
}