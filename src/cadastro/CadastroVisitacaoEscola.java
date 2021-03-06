/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cadastro;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import entidade.Crianca;
import entidade.DiarioDeBordo;
import entidade.Escola;
import entidade.ItemDiarioDeBordo;
import entidade.Monitor;
import entidade.Periodo;
import entidade.Responsavel;
import entidade.VisitacaoEscola;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import persistencia.Dao;

/**
 *
 * @author Ivanildo
 */
public class CadastroVisitacaoEscola extends Application {

    private AnchorPane pane;
    private static Stage stage;

    private Label lData;

    private JFXDatePicker cData;

    private JFXComboBox cbPeriodo;
    private JFXComboBox cbMonitor;
    private JFXComboBox cbEscola;
    private JFXTextField txProfessor;
    private JFXTextField txAtivMinistradas;
    private JFXTextField txFaixaEtaria;
    private JFXTextField txAluno;

    //private List<Escola> escolas = ;
    private ObservableList<Escola> listItens = FXCollections.observableArrayList(Dao.listar(Escola.class));

    private ObservableList<Monitor> monitores = FXCollections.observableArrayList(Dao.listar(Monitor.class));

    private List<String> alunos = new LinkedList<>();
    //ObservableList<String[]> alunos = FXCollections.observableArrayList();

    private JFXButton addAluno;
    private JFXButton removeAluno;

    private JFXButton cadEscola;

    private JFXButton btCadastrar;
    private JFXButton btCancelar;

    private JFXListView<String> listaAlunos;

    private Monitor monitor;

    private VisitacaoEscola visitaEscola = null;

    private DiarioDeBordo diario;

    public void setMonitor(Monitor m) {
        monitor = m;
    }

    public void setVisitaEscola(VisitacaoEscola ve) {
        visitaEscola = ve;
    }

    @Override
    public void start(Stage parent) throws Exception {
        initComponents();
        initListeners();
        if (visitaEscola != null) {
            preencheTela();
        }
        Scene scene = new Scene(pane);
        scene.getStylesheets().add("css/style.css");
        stage.setTitle("Cadastro de visita de escola");

        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL); //mantém o foco nessa tela enquanto aberta

        initLayout();
        stage.initOwner(parent);
        stage.showAndWait();
    }

    private void initComponents() {
        stage = new Stage();
        pane = new AnchorPane();
        pane.setPrefSize(600, 470); //definindo o tamanho da janela de Login
        pane.getStyleClass().add("pane");

        lData = new Label("Data");
        pane.getChildren().add(lData);

        cData = new JFXDatePicker();
        cData.setEditable(false);
        cData.setValue(new Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        cData.setPromptText("Selecione o Dia");
        pane.getChildren().add(cData);
        cbPeriodo = new JFXComboBox(FXCollections.observableArrayList(Periodo.values()));
        cbPeriodo.setFocusColor(Paint.valueOf("#009999"));
        cbPeriodo.setPromptText("Período");
        cbPeriodo.setLabelFloat(true);
        pane.getChildren().add(cbPeriodo);
        cbMonitor = new JFXComboBox(monitores);
        cbMonitor.setFocusColor(Paint.valueOf("#009999"));
        cbMonitor.setPrefWidth(220);
        cbMonitor.setPromptText("Monitor");
        cbMonitor.setLabelFloat(true);
        cbMonitor.getSelectionModel().select(monitor);
        cbMonitor.setDisable(true);
        pane.getChildren().add(cbMonitor);
        cbEscola = new JFXComboBox(listItens);
        cbEscola.setFocusColor(Paint.valueOf("#009999"));
        cbEscola.setPrefWidth(220);
        cbEscola.setPromptText("Escola");
        cbEscola.setLabelFloat(true);
        pane.getChildren().add(cbEscola);
        txProfessor = new JFXTextField();
        txProfessor.setFocusColor(Paint.valueOf("#009999"));
        txProfessor.setPrefWidth(220);
        txProfessor.setPromptText("Professor");
        txProfessor.setLabelFloat(true);
        pane.getChildren().add(txProfessor);
        txAtivMinistradas = new JFXTextField();
        txAtivMinistradas.setFocusColor(Paint.valueOf("#009999"));
        txAtivMinistradas.setPrefWidth(220);
        txAtivMinistradas.setPromptText("Atividades Ministradas");
        txAtivMinistradas.setLabelFloat(true);
        pane.getChildren().add(txAtivMinistradas);

        txFaixaEtaria = new JFXTextField();
        txFaixaEtaria.setFocusColor(Paint.valueOf("#009999"));
        txFaixaEtaria.setPrefWidth(220);
        txFaixaEtaria.setPromptText("Faixa etária das crianças");
        txFaixaEtaria.setLabelFloat(true);
        pane.getChildren().add(txFaixaEtaria);

        txAluno = new JFXTextField();
        txAluno.setFocusColor(Paint.valueOf("#009999"));
        txAluno.setPrefWidth(220);
        txAluno.setPromptText("Digite o nome do Aluno");
        pane.getChildren().add(txAluno);

        addAluno = new JFXButton("Adicionar Aluno");
        addAluno.getStyleClass().add("btAddAluno");
        pane.getChildren().add(addAluno);

        removeAluno = new JFXButton("Remover");
        removeAluno.getStyleClass().add("btRemoveAluno");
        pane.getChildren().add(removeAluno);

        cadEscola = new JFXButton("+");
        cadEscola.getStyleClass().add("btAddEscola");
        pane.getChildren().add(cadEscola);

        listaAlunos = new JFXListView();
        listaAlunos.setPrefSize(605, 170);
        pane.getChildren().add(listaAlunos);

        btCadastrar = new JFXButton("Cadastrar");
        btCadastrar.getStyleClass().add("btCadastrar");
        pane.getChildren().add(btCadastrar);
        btCancelar = new JFXButton("Cancelar");
        btCancelar.getStyleClass().add("btCancelar");
        pane.getChildren().add(btCancelar);

    }

    private void initLayout() {
        lData.setLayoutX(10);
        lData.setLayoutY(25);

        cData.setLayoutX(90);
        cData.setLayoutY(20);
        cbMonitor.setLayoutX(370);
        cbMonitor.setLayoutY(20);
        cbPeriodo.setLayoutX(370);
        cbPeriodo.setLayoutY(70);
        txProfessor.setLayoutX(10);
        txProfessor.setLayoutY(70);
        cbEscola.setLayoutX(370);
        cbEscola.setLayoutY(120);
        cadEscola.setLayoutX(590);
        cadEscola.setLayoutY(120);
        txAtivMinistradas.setLayoutX(10);
        txAtivMinistradas.setLayoutY(120);
        txFaixaEtaria.setLayoutX(10);
        txFaixaEtaria.setLayoutY(170);
        txAluno.setLayoutX(10);
        txAluno.setLayoutY(230);

        addAluno.setLayoutX(235);
        addAluno.setLayoutY(230);
        removeAluno.setLayoutX(340);
        removeAluno.setLayoutY(230);

        listaAlunos.setLayoutX(10);
        listaAlunos.setLayoutY(260);

        btCadastrar.setLayoutX(547);
        btCadastrar.setLayoutY(445);
        btCancelar.setLayoutX(480);
        btCancelar.setLayoutY(445);
    }

    private void initListeners() {
        addAluno.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //tabela.setItems(FXCollections.observableArrayList(txAluno.getText()));
                if (!txAluno.getText().isEmpty()) {
                    //tabela.getItems().add(txAluno.getText());
                    alunos.add(txAluno.getText());
                    listaAlunos.getItems().add(txAluno.getText());
                    txAluno.setText("");
                } else {
                    //JOptionPane.showMessageDialog(null, "Informe o nome do Aluno");
                    new Alert(Alert.AlertType.NONE, "Informe o nome do Aluno", ButtonType.OK).show();
                }
            }
        });

        txAluno.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!txAluno.getText().isEmpty()) {
                    alunos.add(txAluno.getText());
                    listaAlunos.getItems().add(txAluno.getText());
                    txAluno.setText(null);
                } else {
                    new Alert(Alert.AlertType.NONE, "Informe o nome do Aluno", ButtonType.OK).show();
                }
            }
        });

        removeAluno.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (listaAlunos.getSelectionModel().getSelectedIndex() != -1) {
                    alunos.remove(listaAlunos.getSelectionModel().getSelectedItem());
                    listaAlunos.getItems().remove(listaAlunos.getSelectionModel().getSelectedItem());
                } else {
                    //JOptionPane.showMessageDialog(null, "Selecione um item na tabela");
                    new Alert(Alert.AlertType.NONE, "Selecione um item na tabela", ButtonType.OK).show();
                }
            }
        });

        btCancelar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CadastroVisitacaoEscola.getStage().close();
            }
        });

        btCadastrar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //LocalDate data = cData.getValue();
                int flag = 0;
                if (visitaEscola == null) {
                    visitaEscola = new VisitacaoEscola();
                    flag = 1;
                }

                //Convertendo LocalDate para Date, que é o formato da data que a classe espera
                visitaEscola.setData(cData.getValue());
                visitaEscola.setPeriodo((Periodo) cbPeriodo.getSelectionModel().getSelectedItem());
                visitaEscola.setMonitor((Monitor) cbMonitor.getSelectionModel().getSelectedItem());
                visitaEscola.setEscola((Escola) cbEscola.getSelectionModel().getSelectedItem());
                visitaEscola.setProfessor(txProfessor.getText());
                visitaEscola.setAtividadesMinistradas(txAtivMinistradas.getText());
                visitaEscola.setFaixaEtariaCriancas(txFaixaEtaria.getText());
                visitaEscola.setAlunos(alunos);

                if (flag == 1) {
                    //Salvando uma ocorrencia ao diario de bordo===================================
                    ItemDiarioDeBordo item = new ItemDiarioDeBordo();
                    item.setDescricao("Visita de Escola");
                    item.setMonitor(monitor);

                    if (Dao.consultarDiarioHoje().isEmpty()) { //Caso não tenha um diario de bordo ja cadastrado no dia, cadastra um novo
                        diario = new DiarioDeBordo();

                        diario.setDia(LocalDate.now());
                        diario.setMonitorAbriu(monitor);
                        Dao.salvar(diario);
                        diario.getOcorrencias().add(item);
                        
                        Dao.salvar(visitaEscola);
                    } else { //Caso tenha um cadastrado edita
                        diario = Dao.consultarDiarioHoje().get(0);
                        if (diario.getMonitorFechou() == null) {
                            if (!diario.getOcorrencias().contains(item)) {
                                diario.getOcorrencias().add(item);
                            }
                            Dao.salvar(visitaEscola);
                        } else {
                            new Alert(Alert.AlertType.ERROR, "O diário de hoje está fechado, Impossível adicionar uma nova visita de escola", ButtonType.OK).showAndWait();

                        }
                    }
                    Dao.salvar(diario);
                }

                CadastroVisitacaoEscola.getStage().close();
            }
        });

        cadEscola.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    new CadastroEscola().start(CadastroVisitacaoEscola.stage);
                } catch (Exception ex) {
                    Logger.getLogger(CadastroVisitacaoEscola.class.getName()).log(Level.SEVERE, null, ex);
                }
                ObservableList<Escola> lisEscola = FXCollections.observableArrayList(Dao.listar(Escola.class));
                cbEscola.setItems(lisEscola);
            }
        });
    }

    public void preencheTela() {
        cData.setValue(visitaEscola.getData());
        cbPeriodo.getSelectionModel().select(visitaEscola.getPeriodo());
        cbMonitor.getSelectionModel().select(visitaEscola.getMonitor());
        cbEscola.getSelectionModel().select(visitaEscola.getEscola());
        txProfessor.setText(visitaEscola.getProfessor());
        txAtivMinistradas.setText(visitaEscola.getAtividadesMinistradas());
        txFaixaEtaria.setText(visitaEscola.getFaixaEtariaCriancas());
        listaAlunos.setItems(FXCollections.observableArrayList(visitaEscola.getAlunos()));
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
