/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cadastro;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import entidade.DiarioDeBordo;
import entidade.ItemDiarioDeBordo;
import entidade.Monitor;
import java.time.LocalDate;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import persistencia.Dao;

/**
 *
 * @author Ivanildo
 */
public class CadastroOcorrencia extends Application {

    private AnchorPane pane;
    private static Stage stage;

    private Label lDescricao;
    private Label lMonitor;

    private TextArea taDescricao;
    private JFXComboBox cbMonitor;

    private JFXButton btCadastrar;
    private JFXButton btCancelar;

    private Monitor monitor;
    private ItemDiarioDeBordo ocorrencia;
    private DiarioDeBordo diario;

    ObservableList<Monitor> listMonitor = FXCollections.observableArrayList(Dao.consultarTodos(Monitor.class));

    public void setMonitor(Monitor m) {
        monitor = m;
    }

    public void setOcorrencia(ItemDiarioDeBordo item) {
        ocorrencia = item;
    }

    public void setDiario(DiarioDeBordo d) {
        diario = d;
    }

    @Override
    public void start(Stage parent) throws Exception {
        initComponents();
        initListeners();
        if (ocorrencia != null) {
            preencheTela();
        }

        Scene scene = new Scene(pane);
        scene.getStylesheets().add("css/style.css");
        stage.setTitle("Cadastro Ocorrência");

        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        initLayout();
        stage.initOwner(parent);
        stage.showAndWait();
    }

    private void initComponents() {
        stage = new Stage();
        pane = new AnchorPane();
        pane.setPrefSize(460, 210);
        pane.getStyleClass().add("pane");

        lDescricao = new Label("Descrição");
        pane.getChildren().add(lDescricao);
        lMonitor = new Label("Monitor");
        pane.getChildren().add(lMonitor);

        taDescricao = new TextArea();
        taDescricao.setWrapText(true); //quebra de linha ao chegar na borda
        taDescricao.setPrefSize(220, 100);
        pane.getChildren().add(taDescricao);
        cbMonitor = new JFXComboBox(listMonitor);
        cbMonitor.setFocusColor(Paint.valueOf("#009999"));
        cbMonitor.getSelectionModel().select(monitor);
        cbMonitor.setDisable(true);
        pane.getChildren().add(cbMonitor);

        btCadastrar = new JFXButton("Cadastrar");
        btCadastrar.getStyleClass().add("btCadastrar");
        pane.getChildren().add(btCadastrar);
        btCancelar = new JFXButton("Cancelar");
        btCancelar.getStyleClass().add("btCancelar");
        pane.getChildren().add(btCancelar);
    }

    private void initLayout() {
        lMonitor.setLayoutX(10);
        lMonitor.setLayoutY(10);
        lDescricao.setLayoutX(10);
        lDescricao.setLayoutY(50);

        cbMonitor.setLayoutX(120);
        cbMonitor.setLayoutY(10);
        taDescricao.setLayoutX(120);
        taDescricao.setLayoutY(50);

        btCadastrar.setLayoutX(385);
        btCadastrar.setLayoutY(177);
        btCancelar.setLayoutX(315);
        btCancelar.setLayoutY(177);
    }

    private void initListeners() {
        btCancelar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CadastroOcorrencia.getStage().close();
            }
        });

        btCadastrar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (diario != null) {
                    if (ocorrencia == null) {
                        ocorrencia = new ItemDiarioDeBordo();
                    }
                    ocorrencia.setMonitor((Monitor) cbMonitor.getSelectionModel().getSelectedItem());
                    ocorrencia.setDescricao(taDescricao.getText());

                    //                if (diarioEdita == null) {
                    //                    DiarioDeBordo diario;
                    //                    if (Dao.consultarDiarioHoje().isEmpty()) {
                    //                        diario = new DiarioDeBordo();
                    //                        diario.setDia(LocalDate.now());
                    //                        diario.setMonitorAbriu(monitor);
                    //                        diario.getOcorrencias().add(ocorrencia);
                    //                        Dao.salvar(diario);
                    //                    } else {
                    //                        diario = Dao.consultarDiarioHoje().get(0);
                    //                        if (!diario.getOcorrencias().contains(ocorrencia)) {
                    //                            diario.getOcorrencias().add(ocorrencia);
                    //                        }
                    //
                    //                        Dao.salvar(diario);
                    //                    }
                    //                }else{
                    //                    if(!diarioEdita.getOcorrencias().contains(ocorrencia)){
                    //                        diarioEdita.getOcorrencias().add(ocorrencia);
                    //                    }
                    //                    Dao.salvar(diarioEdita);
                    //                }
                    if (!diario.getOcorrencias().contains(ocorrencia)) {
                        diario.getOcorrencias().add(ocorrencia);
                    }
                    Dao.salvar(diario);
                    CadastroOcorrencia.getStage().close();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Diario não existe!", ButtonType.OK);
                }
            }
        });
    }

    public void preencheTela() {
        cbMonitor.getSelectionModel().select(ocorrencia.getMonitor());
        taDescricao.setText(ocorrencia.getDescricao());
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
