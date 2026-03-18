import banco.FabricaConexao;
import visao.MenuTerminal;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando banco de dados...");
        FabricaConexao.inicializarBanco();

        MenuTerminal menu = new MenuTerminal();
        menu.iniciar();
    }
}