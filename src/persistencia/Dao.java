package persistencia;

import entidade.Brinquedo;
import entidade.Classificacao;
import entidade.Crianca;
import entidade.DiarioDeBordo;
import entidade.Escola;
import entidade.Livro;
import entidade.Monitor;
import entidade.Responsavel;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.persistence.*;
import javax.persistence.spi.PersistenceProvider;
//import javax.persistence.Query;
import javax.swing.JOptionPane;

/**
 *
 * @author Ivanildo
 */
public class Dao {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("BrinquedotecaPU");
    private static final EntityManager em = emf.createEntityManager();
    private static final EntityTransaction trx = em.getTransaction();

    private Dao() {

    }

    public static void salvar(Object o) {
        try {
            trx.begin();
            
            if (em.contains(o)) {
                em.merge(o);
            } else {
                em.persist(o);
            }
            
            trx.commit();
            
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar no Banco", ButtonType.OK).showAndWait();
        }
    }

    public static void remover(Object o) throws SQLIntegrityConstraintViolationException {
        trx.begin();
        em.remove(o);
        trx.commit();
    }

    public static List listar(Class c) {
        Query q = em.createQuery("Select o from " + c.getSimpleName() + " o"); //getSimpleName busca objeto de qualquer classe
        return q.getResultList(); //Retorna uma lista de objetos do banco
    }
    
     public static Object busca(Integer id, Class c){ //Busca uma tupla do banco
        return em.find(c, id);
    }
    
    public static  List consultarTodos(Class c){
        Query q = em.createNamedQuery(c.getSimpleName()+".consultarTodos");
        
        return q.getResultList();
    }

    public static List<DiarioDeBordo> consultarDiarioHoje(){
        List<DiarioDeBordo> diarios;
        Query q = em.createNamedQuery("DiarioDeBordo.consultarHoje");
        diarios = q.getResultList();
        return diarios;
    }
}
