package dao;

import banco.FabricaConexao;
import model.Ficha;
import model.ItemFicha;
import java.sql.*;
import java.util.ArrayList; // Adicionado para funcionar a lista
import java.util.List;      // Adicionado para funcionar a lista

public class FichaDAO {

    public void salvarFicha(Ficha ficha) {
        String sqlFicha = "INSERT INTO fichas(aluno_id, semana, observacoes_medicas) VALUES(?, ?, ?)";
        String sqlItem = "INSERT INTO itens_ficha(ficha_id, exercicio_id, dia_semana, series, repeticoes, carga) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = FabricaConexao.getConexao()) {
            conn.setAutoCommit(false); // Inicia transação

            try (PreparedStatement stmtFicha = conn.prepareStatement(sqlFicha, Statement.RETURN_GENERATED_KEYS)) {
                stmtFicha.setInt(1, ficha.getAlunoId());
                stmtFicha.setString(2, ficha.getSemana());
                stmtFicha.setString(3, ficha.getObservacoesMedicas());
                stmtFicha.executeUpdate();

                ResultSet rs = stmtFicha.getGeneratedKeys();
                int fichaId = rs.next() ? rs.getInt(1) : -1;

                try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                    for (ItemFicha item : ficha.getItens()) {
                        stmtItem.setInt(1, fichaId);
                        stmtItem.setInt(2, item.getExercicioId());
                        stmtItem.setString(3, item.getDiaSemana());
                        stmtItem.setInt(4, item.getSeries());
                        stmtItem.setInt(5, item.getRepeticoes());
                        stmtItem.setDouble(6, item.getCarga());
                        stmtItem.executeUpdate();
                    }
                }
                conn.commit(); // Confirma tudo se não houver erro
                System.out.println("✅ Ficha de treino montada e salva com sucesso!");
            } catch (SQLException ex) {
                conn.rollback(); // Desfaz tudo se der erro em algum exercício
                System.err.println("Erro ao salvar itens. Transação revertida: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        }
    }

    public Ficha buscarFichaRecentePorAluno(int alunoId) {
        Ficha ficha = null;
        String sqlFicha = "SELECT id, semana, observacoes_medicas FROM fichas WHERE aluno_id = ? ORDER BY id DESC LIMIT 1";
        // INNER JOIN para pegar o nome popular do exercício lá na outra tabela
        String sqlItens = "SELECT i.dia_semana, e.nome_popular, i.series, i.repeticoes, i.carga " +
                "FROM itens_ficha i JOIN exercicios e ON i.exercicio_id = e.id " +
                "WHERE i.ficha_id = ? ORDER BY i.dia_semana";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement stmtFicha = conn.prepareStatement(sqlFicha)) {

            stmtFicha.setInt(1, alunoId);
            ResultSet rsFicha = stmtFicha.executeQuery();

            if (rsFicha.next()) {
                ficha = new Ficha();
                ficha.setId(rsFicha.getInt("id"));
                ficha.setAlunoId(alunoId);
                ficha.setSemana(rsFicha.getString("semana"));
                ficha.setObservacoesMedicas(rsFicha.getString("observacoes_medicas"));

                try (PreparedStatement stmtItens = conn.prepareStatement(sqlItens)) {
                    stmtItens.setInt(1, ficha.getId());
                    ResultSet rsItens = stmtItens.executeQuery();
                    while (rsItens.next()) {
                        ItemFicha item = new ItemFicha(
                                rsItens.getString("dia_semana"),
                                rsItens.getString("nome_popular"),
                                rsItens.getInt("series"),
                                rsItens.getInt("repeticoes"),
                                rsItens.getDouble("carga")
                        );
                        ficha.adicionarItem(item);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar ficha: " + e.getMessage());
        }
        return ficha;
    }

    // --- MÉTODO NOVO ADICIONADO AQUI ---
    public List<Ficha> listarTodasResumo() {
        List<Ficha> lista = new ArrayList<>();
        // O JOIN junta a tabela fichas (f) com alunos (a) usando o ID
        String sql = "SELECT f.id, f.semana, a.nome " +
                "FROM fichas f " +
                "JOIN alunos a ON f.aluno_id = a.id " +
                "ORDER BY f.id DESC"; // Mostra as mais recentes primeiro

        try (Connection conn = FabricaConexao.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ficha f = new Ficha();
                f.setId(rs.getInt("id"));
                f.setSemana(rs.getString("semana"));
                f.setNomeAlunoTemporario(rs.getString("nome")); // Guarda o nome do aluno
                lista.add(f);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar fichas: " + e.getMessage());
        }
        return lista;
    }

    // --- NOVO MÉTODO: BUSCAR TODAS AS FICHAS COM OS EXERCÍCIOS ---
    public List<Ficha> listarTodasCompletas() {
        List<Ficha> listaFichas = new ArrayList<>();

        // 1. Busca os dados principais da ficha e o nome do aluno
        String sqlFicha = "SELECT f.id, f.semana, f.observacoes_medicas, a.nome " +
                "FROM fichas f " +
                "JOIN alunos a ON f.aluno_id = a.id " +
                "ORDER BY f.id DESC";

        // 2. Query preparada para buscar os itens (exercícios) de cada ficha
        String sqlItens = "SELECT i.dia_semana, e.nome_popular, i.series, i.repeticoes, i.carga " +
                "FROM itens_ficha i " +
                "JOIN exercicios e ON i.exercicio_id = e.id " +
                "WHERE i.ficha_id = ? ORDER BY i.dia_semana";

        try (Connection conn = FabricaConexao.getConexao();
             Statement stmtFicha = conn.createStatement();
             ResultSet rsFicha = stmtFicha.executeQuery(sqlFicha)) {

            while (rsFicha.next()) {
                Ficha ficha = new Ficha();
                ficha.setId(rsFicha.getInt("id"));
                ficha.setSemana(rsFicha.getString("semana"));
                ficha.setObservacoesMedicas(rsFicha.getString("observacoes_medicas"));
                ficha.setNomeAlunoTemporario(rsFicha.getString("nome"));

                // Dentro do loop, usamos o ID da ficha atual para pescar os exercícios dela
                try (PreparedStatement stmtItens = conn.prepareStatement(sqlItens)) {
                    stmtItens.setInt(1, ficha.getId());
                    ResultSet rsItens = stmtItens.executeQuery();
                    while (rsItens.next()) {
                        ItemFicha item = new ItemFicha(
                                rsItens.getString("dia_semana"),
                                rsItens.getString("nome_popular"),
                                rsItens.getInt("series"),
                                rsItens.getInt("repeticoes"),
                                rsItens.getDouble("carga")
                        );
                        ficha.adicionarItem(item);
                    }
                }
                // Adiciona a ficha completa (já com a lista de itens) na lista final
                listaFichas.add(ficha);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar fichas completas: " + e.getMessage());
        }
        return listaFichas;
    }
}