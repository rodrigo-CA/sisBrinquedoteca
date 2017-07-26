package listagem;

import cadastro.CadastroBrinquedo;
import com.jfoenix.controls.JFXButton;
import entidade.Brinquedo;
import entidade.Monitor;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import persistencia.Dao;

public class ListarBrinquedo extends Application{
    private AnchorPane pane;
    private TextField txPesquisa;
    private TableView tabela;
    private JFXButton bEditar;
    private JFXButton bRemover;
    private JFXButton bSair;
    private static Stage stage;
    
    private Monitor monitor;

    TableColumn colunaId;
    TableColumn colunaNome;
    TableColumn colunaFabricante;
    TableColumn colunaEstado;
    TableColumn colunaFaixaEtaria;
    TableColumn colunaClassificacao;

    List<Brinquedo> brinquedos = Dao.listar(Brinquedo.class);
    ObservableList<Brinquedo> listItens = FXCollections.observableArrayList(brinquedos);
    
    public void setMonitor(Monitor m){
        monitor = m;
    }

    @Override
    public void start(Stage parent) {
        initComponents();

        initListeners();
        initLayout();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Tabela Livro");
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

        //responsaveis = Dao.listar(Responsavel.class);
        tabela = new TableView<>();
        colunaId = new TableColumn<>("Id");
        colunaNome = new TableColumn<>("Nome");
        colunaFabricante = new TableColumn<>("Fabricante");
        colunaEstado = new TableColumn<>("Estado");
        colunaFaixaEtaria = new TableColumn<>("Observações");
        colunaClassificacao = new TableColumn<>("Classificação");

        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaFabricante.setCellValueFactory(new PropertyValueFactory<>("fabricante"));
        colunaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colunaFaixaEtaria.setCellValueFactory(new PropertyValueFactory<>("faixaEtaria"));
        colunaClassificacao.setCellValueFactory(new PropertyValueFactory<>("classificacao"));

        tabela.setItems(listItens);
        tabela.setPrefSize(785, 400);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //Colunas se posicionam comforme o tamanho da tabela

        tabela.getColumns().addAll(colunaId, colunaNome, colunaFabricante, colunaEstado, colunaClassificacao, colunaFaixaEtaria);
        pane.getChildren().addAll(tabela, txPesquisa, bSair, bEditar);
        if(monitor.getSupervisor())
            pane.getChildren().add(bRemover);
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

    public void initListeners() {
        txPesquisa.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!txPesquisa.getText().equals("")) {
                    tabela.setItems(findItens());
                }
            }
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
                    
                    
                    tabela.refresh();
                } else {
                    JOptionPane.showMessageDialog(null, "Nenhum item selecionado na tabela");
                }
            }
        });

        bRemover.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (tabela.getSelectionModel().getSelectedIndex() != -1) {
                    if (JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover o item selecionado?") == 0) {
                        
                        try {
                            Dao.remover((Brinquedo) tabela.getSelectionModel().getSelectedItem());
                        } catch (SQLIntegrityConstraintViolationException ex) {
                            Logger.getLogger(ListarBrinquedo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        if(!Dao.listar(Brinquedo.class).contains(tabela.getSelectionModel().getSelectedItem()))
                            tabela.getItems().remove(tabela.getSelectionModel().getSelectedItem());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Nenhum item selecionado");
                }
            }
        });
    }

    private ObservableList<Brinquedo> findItens() {
        ObservableList<Brinquedo> itensEncontrados = FXCollections.observableArrayList();

        for (int i = 0; i < listItens.size(); i++) {
            if (listItens.get(i).getNome().equals(txPesquisa.getText())) {
                itensEncontrados.add(listItens.get(i));
            }
        }
        return itensEncontrados;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
