package banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class FabricaConexao {
    private static final String URL = "jdbc:sqlite:academia.db";

    public static Connection getConexao() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        // Habilita a checagem de chaves estrangeiras no SQLite
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void inicializarBanco() {
        String sqlAlunos = """
            CREATE TABLE IF NOT EXISTS alunos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                cpf TEXT,
                altura INTEGER,
                data_vencimento TEXT
            );
        """;

        String sqlExercicios = """
            CREATE TABLE IF NOT EXISTS exercicios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome_tecnico TEXT,
                nome_popular TEXT,
                parte_corpo TEXT
            );
        """;

        String sqlFichas = """
            CREATE TABLE IF NOT EXISTS fichas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                aluno_id INTEGER,
                semana TEXT,
                observacoes_medicas TEXT,
                peso REAL,
                percentual_gordura REAL,
                massa_magra REAL,
                FOREIGN KEY (aluno_id) REFERENCES alunos(id)
            );
        """;

        String sqlItens = """
            CREATE TABLE IF NOT EXISTS itens_ficha (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ficha_id INTEGER,
                exercicio_id INTEGER,
                dia_semana TEXT,
                series INTEGER,
                repeticoes INTEGER,
                carga REAL,
                FOREIGN KEY (ficha_id) REFERENCES fichas(id),
                FOREIGN KEY (exercicio_id) REFERENCES exercicios(id)
            );
        """;

        try (Connection conn = getConexao(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlAlunos);
            stmt.execute(sqlExercicios);
            stmt.execute(sqlFichas);
            stmt.execute(sqlItens);
            System.out.println("Banco de dados pronto para uso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar banco de dados: " + e.getMessage());
        }
    }
}