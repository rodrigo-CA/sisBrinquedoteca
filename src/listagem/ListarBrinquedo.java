package listagem;

import app.ItemBrinquedo;
import app.ItemCrianca;
import cadastro.CadastroBrinquedo;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import entidade.Brinquedo;
import entidade.Crianca;
import entidade.Monitor;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.persistence.RollbackException;
import persistencia.Dao;

public class ListarBrinquedo extends Application {

    private AnchorPane pane;
    private TextField txPesquisa;
    private TableView<Brinquedo> tabela;
    private JFXButton bEditar;
    private JFXButton bRemover;
    private JFXButton bSair;
    private JFXCheckBox chDetalhes;
    private static Stage stage;

    private Monitor monitor;

    TableColumn colunaId;
    TableColumn colunaNome;
    TableColumn colunaFabricante;
    TableColumn colunaEstado;
    TableColumn colunaFaixaEtaria;
    TableColumn colunaClassificacao;

    ObservableList<Brinquedo> brinquedos;
    //ObservableList<Brinquedo> listItens = FXCollections.observableArrayList(brinquedos);
    
    @Override
    public void start(Stage parent) {
        initComponents();
        initValues();
        initListeners();
        initLayout();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Relatório de Brinquedos");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
        stage.showAndWait();
    }

    public void initComponents() {
        stage = new Stage();
        pane = new AnchorPane();
        pane.setPrefSize(795, 445);

        txPesquisa = new TextField();
        txPesquisa.setPromptText("Pesquisar");

        bEditar = new JFXButton("Editar");

        bRemover = new JFXButton("Remover");

        bSair = new JFXButton("Sair");

        tabela = new TableView<>();
        colunaId = new TableColumn<>("Id");
        colunaNome = new TableColumn<>("Nome");
        colunaFabricante = new TableColumn<>("Fabricante");
        colunaEstado = new TableColumn<>("Estado");
        colunaFaixaEtaria = new TableColumn<>("Faixa Etária");
        colunaClassificacao = new TableColumn<>("Classificação");

        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaFabricante.setCellValueFactory(new PropertyValueFactory<>("fabricante"));
        colunaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colunaFaixaEtaria.setCellValueFactory(new PropertyValueFactory<>("faixaEtaria"));
        colunaClassificacao.setCellValueFactory(new PropertyValueFactory<>("classificacao"));

        tabela.setPrefSize(785, 400);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //Colunas se posicionam comforme o tamanho da tabela

        tabela.getColumns().addAll(colunaId, colunaNome, colunaFabricante, colunaEstado, colunaClassificacao, colunaFaixaEtaria);
        pane.getChildren().addAll(tabela, txPesquisa, bSair, bEditar, bRemover);
    }
    
    private void initValues() {
        brinquedos = FXCollections.observableArrayList(Dao.listar(Brinquedo.class));
        tabela.setItems(brinquedos);
        tabela.refresh();
    }

    public void initLayout() {
        bSair.setLayoutX(10);
        bSair.setLayoutY(10);
        bEditar.setLayoutX(50);
        bEditar.setLayoutY(10);
        bRemover.setLayoutX(100);
        bRemover.setLayoutY(10);

        tabela.setLayoutX(10);
        tabela.setLayoutY(45);
        txPesquisa.setLayoutX(645);
        txPesquisa.setLayoutY(10);
    }
    
    public static Stage getStage() {
        return stage;
    }

    public void initListeners() {

        FilteredList<Brinquedo> filteredData = new FilteredList<>(brinquedos, (e) -> true);
        txPesquisa.setOnKeyReleased((e) -> {
            txPesquisa.textProperty().addListener((observableValue, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super Brinquedo>) user -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if ((user.getId() + "").contains(newValue)) {
                        return true;
                    } else if (user.getNome().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if ((user.getEstado() + "").toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (user.getFabricante().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }

                    return false;
                });
            });
            SortedList<Brinquedo> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tabela.comparatorProperty());
            tabela.setItems(sortedData);
        });

        bSair.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ListarBrinquedo.stage.hide();
            }
        });

        bEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CadastroBrinquedo cb = new CadastroBrinquedo();
                if (tabela.getSelectionModel().getSelectedIndex() != -1) {
                    cb.setBrinquedo((Brinquedo) tabela.getSelectionModel().getSelectedItem());

                    try {
                        cb.start(ListarBrinquedo.stage);
                    } catch (Exception ex) {
                        Logger.getLogger(ListarBrinquedo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    brinquedos.setAll(Dao.listar(Brinquedo.class));
                    tabela.requestFocus();
                } else {
                    new Alert(Alert.AlertType.NONE, "Selecione um item na tabela!", ButtonType.OK).showAndWait();
                    tabela.requestFocus();
                }
            }
        });

        bRemover.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (tabela.getSelectionModel().getSelectedIndex() != -1) {
                    if (new Alert(Alert.AlertType.NONE, "Tem certeza que deseja remover o item selecionado?",ButtonType.CANCEL,ButtonType.YES).showAndWait().get().equals(ButtonType.YES)) {

                        try {
                            Dao.remover((Brinquedo) tabela.getSelectionModel().getSelectedItem());
                        } catch (SQLIntegrityConstraintViolationException ex) {
                            Logger.getLogger(ListarBrinquedo.class.getName()).log(Level.SEVERE, null, ex);
                        } catch(RollbackException rb){
                            new Alert(Alert.AlertType.ERROR, "Impossível remover o item selecionado, pois o mesmo está inserindo em um diário de bordo", ButtonType.OK).show();
                        }
                        brinquedos.setAll(Dao.listar(Brinquedo.class));
                        tabela.requestFocus();
                    }
                } else {
                    new Alert(Alert.AlertType.NONE, "Selecione um item na tabela!", ButtonType.OK).showAndWait();
                    tabela.requestFocus();
                }
            }
        });
        
        tabela.setRowFactory(tv -> {
            TableRow<Brinquedo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ItemBrinquedo.setBrinquedo(row.getItem());
                    try {
                        new ItemBrinquedo().start(ListarBrinquedo.getStage());
                    } catch (Exception ex) {
                        Logger.getLogger(ListarBrinquedo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    initValues();
                }

            });
            return row;
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}
