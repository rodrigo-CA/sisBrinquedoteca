package cadastro;

import app.Principal;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import entidade.Monitor;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import persistencia.Dao;

public class CadastroMonitor extends Application {

    private AnchorPane pane;
    private static Stage stage;

    private Label lSupervisor;

    private JFXTextField txNome;
    private JFXTextField txNomeUser;
    private JFXPasswordField pfSenha;
    private JFXPasswordField pfComfirmacaoSenha;
    private JFXCheckBox chSupervisor;

    private JFXButton btCadastrar;
    private JFXButton btCancelar;
    
    private Monitor monitor;
    
    public void setMonitor(Monitor m){
        monitor = m;
    }

    @Override
    public void start(Stage parent) throws Exception {
        initComponents();
        initListeners();
        if(monitor != null){
            preencheTela();
        }
        Scene scene = new Scene(pane);
        scene.getStylesheets().add("css/style.css");
        stage.setTitle("Cadastro Monitor");

        stage.setScene(scene);
        stage.setResizable(false);
        if(qtdMonitores() > 0){
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        initLayout();
        stage.initOwner(parent);
        //CadastroMonitor.stage = stage;
        stage.showAndWait();
    }

    private void initComponents() {
        stage = new Stage();
        pane = new AnchorPane();
        pane.setPrefSize(550, 290); //definindo o tamanho da janela de Login
        pane.getStyleClass().add("pane");

        lSupervisor = new Label("Supervisor");
        pane.getChildren().add(lSupervisor);

        txNome = new JFXTextField();
        txNome.setFocusColor(Paint.valueOf("#009999"));
        txNome.setPrefWidth(220);
        txNome.setPromptText("Nome");
        txNome.setLabelFloat(true);
        pane.getChildren().add(txNome);
        txNomeUser = new JFXTextField();
        txNomeUser.setFocusColor(Paint.valueOf("#009999"));
        txNomeUser.setPrefWidth(220);
        txNomeUser.setPromptText("Nome de Usuário");
        txNomeUser.setLabelFloat(true);
        pane.getChildren().add(txNomeUser);
        pfSenha = new JFXPasswordField();
        pfSenha.setFocusColor(Paint.valueOf("#009999"));
        pfSenha.setPrefWidth(220);
        pfSenha.setPromptText("Senha");
        pfSenha.setLabelFloat(true);
        pane.getChildren().add(pfSenha);
        pfComfirmacaoSenha = new JFXPasswordField();
        pfComfirmacaoSenha.setFocusColor(Paint.valueOf("#009999"));
        pfComfirmacaoSenha.setPrefWidth(220);
        pfComfirmacaoSenha.setPromptText("Confirmação de Senha");
        pfComfirmacaoSenha.setLabelFloat(true);
        pane.getChildren().add(pfComfirmacaoSenha);

        chSupervisor = new JFXCheckBox();
        pane.getChildren().add(chSupervisor);
        
        primeiraExecucao();

        btCadastrar = new JFXButton("Cadastrar");
        btCadastrar.getStyleClass().add("btCadastrar");
        pane.getChildren().add(btCadastrar);
        btCancelar = new JFXButton("Cancelar");
        btCancelar.getStyleClass().add("btCancelar");
        pane.getChildren().add(btCancelar);
    }

    private void initLayout() {
        lSupervisor.setLayoutX(10);
        lSupervisor.setLayoutY(150);

        txNome.setLayoutX(10);
        txNome.setLayoutY(15);
        txNomeUser.setLayoutX(10);
        txNomeUser.setLayoutY(100);
        pfSenha.setLayoutX(290);
        pfSenha.setLayoutY(15);
        pfComfirmacaoSenha.setLayoutX(290);
        pfComfirmacaoSenha.setLayoutY(100);
        chSupervisor.setLayoutX(70);
        chSupervisor.setLayoutY(150);

        btCadastrar.setLayoutX(485);
        btCadastrar.setLayoutY(270);
        btCancelar.setLayoutX(415);
        btCancelar.setLayoutY(270);
    }

    private void initListeners() {
        btCancelar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CadastroMonitor.getStage().close();
            }
        });

        btCadastrar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (pfSenha.getText().equals(pfComfirmacaoSenha.getText())) {
                    if(monitor == null)
                        monitor = new Monitor();
                    monitor.setNome(txNome.getText());
                    monitor.setNomeUsuario(txNomeUser.getText());
                    monitor.setSenha(pfSenha.getText());
                    monitor.setSupervisor(chSupervisor.isSelected());

                    Dao.salvar(monitor);
                    CadastroMonitor.getStage().hide();

                    if (Dao.consultarTodos(Monitor.class).size() == 1) {
                        Principal principal = new Principal();
                        principal.setMonitor(monitor);
                        principal.start(CadastroMonitor.stage);
                    }
                } else {
                    final String cssDefault = "-fx-border-color: red;-fx-border-width: 1;";
                    pfComfirmacaoSenha.setStyle(cssDefault);
                    JOptionPane.showMessageDialog(null, "Os campos senha e confirmação de senha devem ser identicos");
                }
            }
        });
    }
    
    public void preencheTela(){
        List<Monitor> monitores = Dao.listar(Monitor.class);
        txNome.setText(monitor.getNome());
        txNomeUser.setText(monitor.getNomeUsuario());
        chSupervisor.setSelected(monitor.getSupervisor());
        chSupervisor.setDisable(true);
        for (Monitor m : monitores) {
            if(m.getSupervisor() && !m.equals(monitor)){
                chSupervisor.setDisable(false);
            }
        }
    }

    public static Stage getStage() {
        return stage;
    }
    
    private int qtdMonitores() {
        int qtdMonitores = Dao.consultarTodos(Monitor.class).size();
        return qtdMonitores;
    }

    private void primeiraExecucao() {
        if (qtdMonitores() <= 0){
            chSupervisor.setSelected(true);
            chSupervisor.setDisable(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
