package listagem;

import cadastro.CadastroVisitacaoEscola;
import com.jfoenix.controls.JFXButton;
import entidade.Monitor;
import entidade.VisitacaoEscola;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import persistencia.Dao;

/**
 *
 * @author Ivanildo
 */
public class ListarVisitaEscola extends Application{
    private AnchorPane pane;
    private TextField txPesquisa;
    private TableView tabela;
    private JFXButton bEditar;
    private JFXButton bRemover;
    private JFXButton bSair;
    private static Stage stage;
    
    private Monitor monitor;

    TableColumn colunaId;
    TableColumn colunaData;
    TableColumn colunaPeriodo;
    TableColumn colunaMonitor;
    TableColumn colunaEscola;

    List<VisitacaoEscola> visitas = Dao.listar(VisitacaoEscola.class);
    ObservableList<VisitacaoEscola> listItens = FXCollections.observableArrayList(visitas);
    
    public void setMonitor(Monitor m){
        monitor = m;
    }

    @Override
    public void start(Stage parent) {
        initComponents();

        initListeners();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Tabela no JavaFX");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
        stage.showAndWait();
    }

    public void initComponents() {
        stage = new Stage();
        pane = new AnchorPane();
        pane.setPrefSize(795, 595);

        txPesquisa = new TextField();
        txPesquisa.setPromptText("Pesquisar");

        bEditar = new JFXButton("Editar");
        pane.getChildren().add(bEditar);

        bRemover = new JFXButton("Remover");
        if(monitor.getSupervisor())
            pane.getChildren().add(bRemover);

        bSair = new JFXButton("Sair");
        pane.getChildren().add(bSair);

        tabela = new TableView<>();
        colunaId = new TableColumn<>("Id");
        colunaData = new TableColumn<>("Data");
        colunaPeriodo = new TableColumn<>("Período");
        colunaMonitor = new TableColumn<>("Monitor");
        colunaEscola = new TableColumn<>("Escola");

        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colunaPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
        colunaMonitor.setCellValueFactory(new PropertyValueFactory<>("monitor"));
        colunaEscola.setCellValueFactory(new PropertyValueFactory<>("escola"));
        
        DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colunaData.setCellFactory(column -> {
            return new TableCell<VisitacaoEscola, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(myDateFormatter.format(item));
                    }
                }
            };
        });
        

        tabela.setItems(listItens);
        tabela.setPrefSize(785, 550);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //Colunas se posicionam comforme o tamanho da tabela

        initLayout();
        tabela.getColumns().addAll(colunaId, colunaData, colunaPeriodo, colunaMonitor, colunaEscola);
        pane.getChildren().addAll(txPesquisa, tabela);
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
                ListarVisitaEscola.stage.hide();
            }
        });

        bEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CadastroVisitacaoEscola cv = new CadastroVisitacaoEscola();
                if (tabela.getSelectionModel().getSelectedIndex() != -1) {
                    cv.setVisitaEscola((VisitacaoEscola) tabela.getSelectionModel().getSelectedItem());
                    try {
                        cv.start(ListarVisitaEscola.stage);
                    } catch (Exception ex) {
                        Logger.getLogger(ListarVisitaEscola.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ObservableList<VisitacaoEscola> list = FXCollections.observableArrayList(Dao.consultarTodos(VisitacaoEscola.class));
                    tabela.getItems().clear();
                    tabela.setItems(list);
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
                            Dao.remover((VisitacaoEscola) tabela.getSelectionModel().getSelectedItem());
                        } catch (SQLIntegrityConstraintViolationException ex) {
                            Logger.getLogger(ListarVisitaEscola.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ObservableList<VisitacaoEscola> list = FXCollections.observableArrayList(Dao.consultarTodos(VisitacaoEscola.class));
                        tabela.getItems().clear();
                        tabela.setItems(list);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Nenhum item selecionado");
                }
            }
        });
    }

    private ObservableList<VisitacaoEscola> findItens() {
        ObservableList<VisitacaoEscola> itensEncontrados = FXCollections.observableArrayList();

        for (int i = 0; i < listItens.size(); i++) {
            if (listItens.get(i).getEscola().equals(txPesquisa.getText())) {
                itensEncontrados.add(listItens.get(i));
            }
        }
        //if(listItens)

        return itensEncontrados;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
