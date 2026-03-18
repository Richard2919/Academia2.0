package dao;

import banco.FabricaConexao;
import model.Exercicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExercicioDAO {
    public void salvar(Exercicio exercicio) {
        String sql = "INSERT INTO exercicios(nome_tecnico, nome_popular, parte_corpo) VALUES(?, ?, ?)";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, exercicio.getNomeTecnico());
            pstmt.setString(2, exercicio.getNomePopular());
            pstmt.setString(3, exercicio.getParteCorpo());
            pstmt.executeUpdate();
            System.out.println("✅ Exercício cadastrado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar exercício: " + e.getMessage());
        }
    }

    public List<Exercicio> listarTodos() {
        List<Exercicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM exercicios";
        try (Connection conn = FabricaConexao.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Exercicio ex = new Exercicio();
                ex.setId(rs.getInt("id"));
                ex.setNomeTecnico(rs.getString("nome_tecnico"));
                ex.setNomePopular(rs.getString("nome_popular"));
                ex.setParteCorpo(rs.getString("parte_corpo"));
                lista.add(ex);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar exercícios: " + e.getMessage());
        }
        return lista;
    }
}